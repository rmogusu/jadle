package dao;

import models.Foodtype;
import models.Restaurant;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import static org.junit.Assert.*;

public class Sql2oFoodtypeDaoTest {
    private static Connection conn;
    private static Sql2oRestaurantDao restaurantDao;
    private static Sql2oFoodtypeDao foodtypeDao;
    private static Sql2oReviewDao reviewDao;

    @Before
    public void setUp() throws Exception {
//        String connectionString = "jdbc:postgresql://localhost:5432/jadle_test";
//        Sql2o sql2o = new Sql2o(connectionString, "rose", "wambua");
        String connectionString = "jdbc:postgresql://ec2-52-71-85-210.compute-1.amazonaws.com:5432/d30at9d8mn3j89"; //!
        Sql2o sql2o = new Sql2o(connectionString, "lwwpqeqpxnbasa", "63e7488a136d9016f8ec96923589c8ea697569ac2e0d87651adf591438deed80"); //!
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        restaurantDao.clearAll();
        reviewDao.clearAll();
        foodtypeDao.clearAll();
        System.out.println("clearing database");
    }

    @AfterClass
    public static void shutDown() throws Exception{ //changed to static
        conn.close();
        System.out.println("connection closed");
    }

    @Test
    public void addingFoodSetsId() throws Exception {
        Foodtype testFoodtype = setupNewFoodtype();
        int originalFoodtypeId = testFoodtype.getId();
        foodtypeDao.add(testFoodtype);
        assertNotEquals(originalFoodtypeId,testFoodtype.getId());
    }
    @Test
    public void addedFoodtypesAreReturnedFromGetAll() throws Exception {
        Foodtype testfoodtype = setupNewFoodtype();
        foodtypeDao.add(testfoodtype);
        assertEquals(1, foodtypeDao.getAll().size());
    }

    @Test
    public void noFoodtypesReturnsEmptyList() throws Exception {
        assertEquals(0, foodtypeDao.getAll().size());
    }

    @Test
    public void deleteByIdDeletesCorrectFoodtype() throws Exception {
        Foodtype foodtype = setupNewFoodtype();
        foodtypeDao.add(foodtype);
        foodtypeDao.deleteById(foodtype.getId());
        assertEquals(0, foodtypeDao.getAll().size());
    }

    @Test
    public void clearAll() throws Exception {
        Foodtype testFoodtype = setupNewFoodtype();
        Foodtype otherFoodtype = setupNewFoodtype();
        foodtypeDao.clearAll();
        assertEquals(0, foodtypeDao.getAll().size());
    }
    @Test
    public void addFoodTypeToRestaurantAddsTypeCorrectly() throws Exception {

        Restaurant testRestaurant = setupRestaurant();
        Restaurant altRestaurant = setupAltRestaurant();

        restaurantDao.add(testRestaurant);
        restaurantDao.add(altRestaurant);

        Foodtype testFoodtype = setupNewFoodtype();

        foodtypeDao.add(testFoodtype);

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, altRestaurant);

        assertEquals(2, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }
    @Test
    public void deleteingRestaurantAlsoUpdatesJoinTable() throws Exception {
        Foodtype testFoodtype  = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);

        Restaurant altRestaurant = setupAltRestaurant();
        restaurantDao.add(altRestaurant);

        restaurantDao.addRestaurantToFoodtype(testRestaurant,testFoodtype);
        restaurantDao.addRestaurantToFoodtype(altRestaurant, testFoodtype);

        restaurantDao.deleteById(testRestaurant.getId());
        assertEquals(0, restaurantDao.getAllFoodtypesByRestaurant(testRestaurant.getId()).size());
    }
    @Test
    public void deletingFoodtypeAlsoUpdatesJoinTable() throws Exception {

        Restaurant testRestaurant = setupRestaurant();

        restaurantDao.add(testRestaurant);

        Foodtype testFoodtype = setupNewFoodtype();
        Foodtype otherFoodType = new Foodtype("Japanese");

        foodtypeDao.add(testFoodtype);
        foodtypeDao.add(otherFoodType);

        foodtypeDao.addFoodtypeToRestaurant(testFoodtype, testRestaurant);
        foodtypeDao.addFoodtypeToRestaurant(otherFoodType,testRestaurant);

        foodtypeDao.deleteById(testRestaurant.getId());
        assertEquals(1, foodtypeDao.getAllRestaurantsForAFoodtype(testFoodtype.getId()).size());
    }
    // helpers

    public Foodtype setupNewFoodtype(){
        return new Foodtype("Sushi");
    }
    public Restaurant setupRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
        restaurantDao.add(restaurant);
        return restaurant;
    }

    public Restaurant setupAltRestaurant (){
        Restaurant restaurant = new Restaurant("Fish Omena", "214 NE Ngara", "97232", "254-402-9874");
        restaurantDao.add(restaurant);
        return restaurant;
    }
}