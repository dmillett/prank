prank
=====

Is a java library suupporting parallel ranking and scoring of Java objects and collections.
Each object in a collection contains a Prankster object that encapsulates score/rank results
for each active ScoreCard. Each result  

##Features
* Parallel scoring of a generic collection of objects with 1 - N ScoreCards
* A result object for every scored object in the collection is available for
** calculated score
** adjusted score
**  

##Usage

```java
ScoreCard<List<BookExample>> priceCard = new PriceScoreCard(minPoints, maxPoints, slices);
ScoreCard<List<BookExample>> deliveryTimeCard = new DeliveryTimeScoreCard(minPoints, maxPoints, slices);

Prankster<List<BookExample>> prankster = buildPrankster(priceCard, deliveryTimeCard);
```

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