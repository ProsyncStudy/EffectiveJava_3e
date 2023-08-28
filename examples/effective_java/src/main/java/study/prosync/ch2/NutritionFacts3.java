// package study.prosync.ch2;

// import lombok.Builder;
// import lombok.Builder.Default;

// @Builder
// public class NutritionFacts3 {
//   private final int servingSize;
//   private final int servings;
//   private final int calories;
//   private final int fat;
//   private final int sodium;
//   private final int carbohydrate;

//   private static NutritionFacts3Builder builder() {
//     return new NutritionFacts3Builder();
//   }

//   public static NutritionFacts3Builder builder(
//     int calories, int fat, int sodium, int carbohydrate) {
//     return builder().calories(calories)
//                     .fat(fat)
//                     .sodium(sodium)
//                     .carbohydrate(carbohydrate);
//   }
// }