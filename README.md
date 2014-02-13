prank
=====

Is a java library supporting asynchronous/parallel ranking and scoring of Java objects and collections.
Add a ScoreSummary to each object in a collection and then define a Set of ScoreCard objects for that
object type. Applies all configured ScoreCard evaluations.

This is useful for distributed transactions where Customer selection is important. For example,
selecting books from used booksellers where price, delivery time, shipping cost, user ratings,
etc may be important. Scoring is completed once in the distributed transaction but produces
data that can be used for sorting (see tallyScore()), personalization (adjusted score from
user preferences), and Machine Learning (summary statistics that help differentiate a selected
option).

##Features
* Parallel scoring of a generic collection of objects with 1 - N ScoreCards
* A result object for every scored object in the collection can contain:
  1. scored value
  2. original position index
  3. ScoreData (calculated score, adjusted Score, min points, max points, number of score buckets)
  4. Statistics average, mean deviation, median deviation, standard deviation
  5. per request scoring conditions
* Each ScoreSummary result may be adjusted at any time after initial scoring

##Usage

See the *example* package in the test directory for other examples. After defining
individual ScoreCard implementations, usage is as simple as:

#### Setup applicable ScoreCard objects
```java
// Establish score cards
ScoreCard<List<BookExample>> priceCard = new PriceScoreCard(minPoints, maxPoints, slices);
ScoreCard<List<BookExample>> deliveryTimeCard = new DeliveryTimeScoreCard(minPoints, maxPoints, slices);

// Setup scoring mechanism (wire up a Prankster with dependency injection)
Prankster<List<BookExample>> prankster = buildPrankster(priceCard, deliveryTimeCard);

// Score the request (futures will return within specified scoring timeout)
Request<List<BookExample> request = new Request<List<BookExample>>();
prankster.updateObjectScore(request, scoringTimeoutInMillis);
```

#### Per request configuration options
```java
// Establish score cards
ScoreCard<List<BookExample>> priceCard = new PriceScoreCard(minPoints, maxPoints, slices);
ScoreCard<List<BookExample>> deliveryTimeCard = new DeliveryTimeScoreCard(minPoints, maxPoints, slices);

// Setup scoring mechanism (wire up a Prankster with dependency injection)
Prankster<List<BookExample>> prankster = buildPrankster(priceCard, deliveryTimeCard);

// Setup per-request overrides for PriceScoreCard
RequestOptions priceCardOptions = new RequestOptions.RequestOptionsBuilder().build();
Map<String, RequestOptions> optionsMap = new HashMap<String, RequestOptions>();
optionsMap.put(priceCard.getName(), priceCardOptions);

// Score the request (PriceCard with overrides, DeliveryCard with default values)
Request<List<BookExample> request = new Request<List<BookExample>>();
prankster.updateObjectScore(request, scoringTimeoutInMillis);
```

#### Unit testing ScoreCard implementations is straight forward
Building unit tests for each ScoreCard implementation is fairly easy. The complexity of the
test cases depends on how complex the ScoreCard scoring algorithm is.

```java
// Relative ranking by price of books in a search result
private List<BookExample> scoreSolutionsByPrice(int minPoints, int maxPoints, int slices) {
 
    List<BookExample> solutions = getSolutionsFromFile();
    ScoreCard<List<BookExample>> card = new PriceScoreCard(minPoints, maxPoints, slices);
    card.score(solutions);
 
    return solutions;
}

// Relative ranking by delivery time estimate of books in a search result
private List<BookExample> scoreSolutionsByDeliveryTime(int minPoints, int maxPoints, int slices) {
 
    List<BookExample> solutions = getSolutionsFromFile();
    ScoreCard<List<BookExample>> card = new DeliveryTimeScoreCard(minPoints, maxPoints, slices);
    card.score(solutions);
 
    return solutions;
}
```
