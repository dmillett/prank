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
* personalization (adjust score from preferences (user/configured/ML/etc)
* Reporting and Machine Learning (summary statistics that help differentiate a selected option).

##Features
* Parallel scoring of a generic collection of objects with 1 - N ScoreCards
  1. A timeout is available for each scoring process, it will score as many as possible in the time
* Each ScoreSummary result may be adjusted/customized during or after initial scoring
* A result object for every scored object in the collection can contain:
  1. scored value
  2. original position index
  3. ScoreData (calculated score, adjusted Score, min points, max points, number of score buckets)
  4. Optional statistics average, mean deviation, median deviation, standard deviation
  5. Optional per request scoring conditions and timeouts
* Each ScoreCard thread pool size should be configured to the handle the maximum number of actual/expected
concurrent requests

##Usage
After adding the dependency to the build, see the examples below or in the **src/test/java/net/prank/example/** package and code directly or use a dependency injection manager (Spring, Guice, etc) & configuration to set scoring values. 

```
<dependency>
  <groupId>com.github.dmillett</groupId>
  <artifactId>prank</artifactId>
  <version>1.1.0</version>
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
// Use the max expected number of concurrent requests for poolSize 
Prankster prankster = new Prankster(scoreCards, maxConcurrentRequestCount);

// Score a List of 'ExampleObject' objects
List<ExampleObject> examples = someSearchRequest();
Request<List<ExampleObject>> request = new Request<List<ExampleObject>>(examples);
prankster.updateObjectsWithScores(request, scoringTimeoutInMillis);
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
// Setup per-request overrides for PriceScoreCard
RequestOptions priceCardOptions = new RequestOptions.RequestOptionsBuilder().build();
Map<String, RequestOptions> optionsMap = new HashMap<String, RequestOptions>();
optionsMap.put(priceCard.getName(), priceCardOptions);

// Score the request (PriceCard with overrides, DeliveryCard with default values)
Request<List<ExampleObject> request = new Request<List<ExampleObject>>();
request.addOptions(optionsMap);
prankster.updateObjectsWithScores(request, scoringTimeoutInMillis);
```

#### Sample ScoreCard
There are three examples in the **src/test/java/net/prank/example** directory. Here is code
from the **PriceScoreCard**. Scoring is an isolated mechanism and can vary by ScoreCard implementation.
For simplicity, **ShippingCostScoreCard** and **ShippingTimeScoreCard** score similarly to PriceScoreCard.

```java
// The statistics are optional (for performance) and could be computed with a library of choice
void updateSolutionsWithScore(List<ExampleObject> solutions, Set<ScoringRange> scoringRange,
                              double average, double standardDeviation, ScoringTool scoringTool) {

    int i = 0;
    for ( ExampleObject solution : solutions )
    {
        if ( solution.getPrice() == null )
        {
            i++;
            continue;
        }

        double totalPrice = solution.getPrice().doubleValue();
        double score = scoringTool.getScoreFromRange(totalPrice, scoringRange);
        ScoreData.Builder scoreBuilder = new ScoreData.Builder();
        scoreBuilder.setScore(new BigDecimal(String.valueOf(score)));

        // Optional statistics
        Statistics.Builder statsBuilder = new Statistics.Builder();
        statsBuilder.setAverage(new BigDecimal(String.valueOf(average)));
        statsBuilder.setStandardDeviation(new BigDecimal(String.valueOf(standardDeviation)));
        Statistics stats = statsBuilder.build();

        Result.Builder rb = new Result.Builder(NAME, scoreBuilder.build());
        rb.setPosition(new Indices(i));
        rb.setOriginal(totalPrice);
        rb.setStatistics(stats);
        Result result = rb.build();

        solution.getScoreSummary().addResult(NAME, result);
        i++;
    }
}
```

#### Unit testing ScoreCard implementations is straight forward
Building unit tests for each ScoreCard implementation is fairly easy. The complexity of the
test cases depends on how complex the ScoreCard scoring algorithm is. This is also beneficial
for building analyst tools to evaluate how scoring strategies affect known results.

```java
// Relative ranking by price of books in a search result (updates each ExampleObject)

@Test
public void test__score_updateObjectsWithScores() {

    PranksterExample pe = new PranksterExample();
    List<ExampleObject> examples = pe.getExamples();
    Request<List<ExampleObject>> request = new Request<List<ExampleObject>>(examples);

    Set<ScoreCard<List<ExampleObject>>> scoreCards = new HashSet<ScoreCard<List<ExampleObject>>>();
    scoreCards.add(new PriceScoreCard(0, 20, 10));

    Prankster<List<ExampleObject>> prankster = new Prankster<List<ExampleObject>>(scoreCards, 1);
    prankster.updateObjectsWithScores(request, 20);

    assertEquals(new BigDecimal("2.0"), examples.get(0).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("16.0"), examples.get(1).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("12.0"), examples.get(2).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("2.0"), examples.get(3).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("20.0"), examples.get(4).getScoreSummary().tallyScore());
    assertEquals(new BigDecimal("8.0"), examples.get(5).getScoreSummary().tallyScore());
}
```
