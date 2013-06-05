prank
=====

Is a java library supporting parallel ranking and scoring of Java objects and collections.
Add a ScoreSummary to each object in a collection and then define a Set of ScoreCard objects
for that object type. plies all ScoreCard evaluations

##Features
* Parallel scoring of a generic collection of objects with 1 - N ScoreCards
* A result object for every scored object in the collection can contain:
  1. calculated score
  2. adjusted score
  3. average
  4. standard deviation
  5. original position in the collection
* Each ScoreSummary result may be adjusted at any time after initial scoring

##Usage

See the *example* package in the test directory for other examples. After defining
individual ScoreCard implementations, usage is as simple as:

```java
ScoreCard<List<BookExample>> priceCard = new PriceScoreCard(minPoints, maxPoints, slices);
ScoreCard<List<BookExample>> deliveryTimeCard = new DeliveryTimeScoreCard(minPoints, maxPoints, slices);

Prankster<List<BookExample>> prankster = buildPrankster(priceCard, deliveryTimeCard);
```

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
