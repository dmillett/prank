prank
=====

Is a java library supporting asynchronous/parallel ranking and scoring of Java objects and collections.
Add a ScoreSummary to each object in a collection and then define a Set of ScoreCard objects for that
object type. Applies all configured ScoreCard evaluations.

This is useful for distributed transactions where Customer selection is important. For example,
selecting books from used booksellers where price, delivery time, shipping cost, user ratings,
etc may be important. Scoring is completed once in the distributed transaction but produces
data that can be used for;
* sorting (see tallyScore()), 
* personalization (adjusted score from user preferences)
* Reporting and Machine Learning (summary statistics that help differentiate a selected option).

##Features
* Parallel scoring of a generic collection of objects with 1 - N ScoreCards
* Each ScoreSummary result may be adjusted/customized during or after initial scoring
* A result object for every scored object in the collection can contain:
  1. scored value
  2. original position index
  3. ScoreData (calculated score, adjusted Score, min points, max points, number of score buckets)
  4. Statistics average, mean deviation, median deviation, standard deviation
  5. per request scoring conditions

##Usage
After adding the dependency to the build, see the examples below or in the *src/test/java/net/prank/example/* package and code directly or use a dependency injection manager (Spring, Guice, etc) & configuration to set scoring values. 

```
<dependency>
  <groupId>com.github.dmillett</groupId>
  <artifactId>prank</artifactId>
  <version>1.0.1</version>
</dependency>  
```

After defining individual ScoreCard implementations, usage is as simple as:
(see src/test/java/net/prank/example package)
#### Setup applicable ScoreCard objects
```java
// Establish score cards
ScoreCard examplePrice = new PriceScoreCard(0, 20, 10);
ScoreCard exampleShippingCost = new ShippingCostScoreCard(0, 10, 10);
ScoreCard exampleShippingTime = new ShippingTimeScoreCard(0, 5, 5);
        
Set<ScoreCard> scoreCards = new HashSet<ScoreCard>();
scoreCards.add(examplePrice);
scoreCards.add(exampleShippingCost);
scoreCards.add(exampleShippingTime);        
                
// Setup scoring mechanism (can wire up a Prankster with dependency injection) 
Prankster prankster = new Prankster(scoreCards, executorPoolSize);

// Score a List of 'ExampleObject' objects
List<ExampleObject> examples = someSearchRequest();
Request<List<ExampleObject>> request = new Request<List<ExampleObject>>(examples);
prankster.updateObjectScore(request, scoringTimeoutInMillis);
```
#### Sort the results according to score
```java
// Sort 'Scorable' results by 'ORIGINAL' score for all ScoreCard objects
Collections.sort(examples, new ScoreComparator());

// Sort 'Scorable' results by 'ADJUSTED' (<- customization) score for PriceScoreCard
Set<String> priceCard = new HashSet<String>();
priceCard.add(PriceScoreCard.NAME);
Collections.sort(examples, new ScoreComparator(priceCard, Result.ResultScoreType.ADJUSTED)); 
```

#### Per request configuration options
```java
// Establish score cards
ScoreCard examplePrice = new PriceScoreCard(0, 20, 10);
ScoreCard exampleShippingCost = new ShippingCostScoreCard(0, 10, 10);
ScoreCard exampleShippingTime = new ShippingTimeScoreCard(0, 5, 5);

// Setup scoring mechanism (can wire up a Prankster with dependency injection) 
Prankster prankster = new Prankster(scoreCards, executorPoolSize);

// Setup per-request overrides for PriceScoreCard
RequestOptions priceCardOptions = new RequestOptions.RequestOptionsBuilder().build();
Map<String, RequestOptions> optionsMap = new HashMap<String, RequestOptions>();
optionsMap.put(priceCard.getName(), priceCardOptions);

// Score the request (PriceCard with overrides, DeliveryCard with default values)
Request<List<ExampleObject> request = new Request<List<ExampleObject>>();
request.addOptions(optionsMap);
prankster.updateObjectScore(request, scoringTimeoutInMillis);
```

#### Unit testing ScoreCard implementations is straight forward
Building unit tests for each ScoreCard implementation is fairly easy. The complexity of the
test cases depends on how complex the ScoreCard scoring algorithm is.

```java
// Relative ranking by price of books in a search result (updates each ExampleObject)

@Test
public void test_PriceScoreCard_for_ExampleObject() {

    PranksterExample pe = new PranksterExample();
    List<ExampleObject> examples = pe.getExamples();
    Request<List<ExampleObject>> request = new Request<List<ExampleObject>>(examples);

    Set<ScoreCard<List<ExampleObject>>> scoreCards = new HashSet<ScoreCard<List<ExampleObject>>>();
    scoreCards.add(new PriceScoreCard(0, 20, 10));

    Prankster<List<ExampleObject>> prankster = new Prankster<List<ExampleObject>>(scoreCards, 1);
    prankster.updateObjectScore(request, 20);

    assertEquals(new BigDecimal("2.0"), examples.get(0).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("16.0"), examples.get(1).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("12.0"), examples.get(2).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("2.0"), examples.get(3).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("20.0"), examples.get(4).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("8.0"), examples.get(5).getScoreSummary().tallyScore());
}
```
