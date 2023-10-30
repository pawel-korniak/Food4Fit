package com.objavieni.mealDistribution;

import com.objavieni.dto.PreferencesDto;
import com.objavieni.meals.DailyMeals;
import com.objavieni.meals.Meal;
import com.objavieni.meals.Recipe;
import com.objavieni.meals.WeeklyMeals;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Component
@Slf4j
@NoArgsConstructor
public class MealDistributor {

    public static final int DAYS_IN_WEEK = 7;
    private List<Meal> mealList;
    private PreferencesDto preferences;
    private WeeklyMeals weeklyMeals;

    public void setDataToCompute(List<Recipe> recipeList, PreferencesDto preferencesDto) {
        log.info("loading preferendes to distributor");
        this.preferences = preferencesDto;
        log.info("loading recipes to meallist");
        this.mealList = recipesToMeals(recipeList);
        log.info("distribute to weeklymeals");
        this.weeklyMeals = distribute();
    }

    public WeeklyMeals distribute() {
        WeeklyMeals weeklyMeals = new WeeklyMeals();
        getDailyMealsForWeek(weeklyMeals, mealList);
        printWeeklyMealsLog(weeklyMeals);
        return weeklyMeals;
    }

    private List<Meal> recipesToMeals(List<Recipe> recipeList) {
        List<Meal> mealList = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            mealList.add(new Meal(recipe));
        }
        List<Meal> list = mealList.stream().sorted(Comparator.comparingInt(x -> x.getCalories())).collect(Collectors.toList());
        return list;
    }

    private void getDailyMealsForWeek(WeeklyMeals weeklyMeals, List<Meal> acceptableMeals) {
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            DailyMeals day = new DailyMeals();
            getMealsForDayExceptLastOne(acceptableMeals, day);
            handleLastMealOfTheDay(acceptableMeals, day);
            weeklyMeals.addDailyMeals(day);
        }
    }

    private void getMealsForDayExceptLastOne(List<Meal> acceptableMeals, DailyMeals day) {
        for (int i = 1; i < preferences.getCountMealsPerDay(); i++) {
            int randomIndex = (int) (Math.random() * acceptableMeals.size());
            day.addMeal(acceptableMeals.get(randomIndex));
            acceptableMeals.remove(randomIndex);
        }
    }

    private void handleLastMealOfTheDay(List<Meal> acceptableMeals, DailyMeals day) {
        Meal lastMeal = getLastMealOfTheDay(day, acceptableMeals);
        day.addMeal(lastMeal);
        acceptableMeals.remove(lastMeal);
    }

    private Meal getLastMealOfTheDay(DailyMeals dailyMeals, List<Meal> meals) {
        int caloriesFromEarlierMeals = 0;
        for (Meal meal : dailyMeals.getMealList()) {
            caloriesFromEarlierMeals += meal.getCalories();
        }
        int expectedCaloriesForLastMeal = preferences.getCountCaloriesPerDay() - caloriesFromEarlierMeals;
        Optional<Meal> lastMealOptional = meals.stream()
                .sorted(Comparator.comparingInt(Meal::getCalories))
                .filter(meal -> meal.getCalories() >= expectedCaloriesForLastMeal)
                .findFirst();

        if(lastMealOptional.isPresent()) return lastMealOptional.get();
        else return meals.get(meals.size() - 1);
    }

    private void printWeeklyMealsLog(WeeklyMeals weeklyMeals) {
        for (DailyMeals dailyMeals : weeklyMeals.getDailyMealsList()) {
            int totalCalories = 0;
            log.info("DAILY MEALS ");
            for (Meal meal : dailyMeals.getMealList()) {
                log.info(meal.getName() + ": " + meal.getCalories() + " calories.");
                totalCalories += meal.getCalories();
            }
            log.info("Total calories: " + totalCalories);
        }
    }

    public WeeklyMeals getWeeklyMeals() {
        return weeklyMeals;
    }
}
