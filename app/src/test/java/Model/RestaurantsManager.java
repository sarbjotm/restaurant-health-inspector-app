/**
 * A manager to store Restaurants.
 */
package Model;

import java.util.ArrayList;

public class RestaurantsManager {
    private ArrayList<Restaurant> restaurantList = new ArrayList<>();
    private static RestaurantsManager instance;

    private RestaurantsManager(){

    }

    public static RestaurantsManager getInstance(){
        if (instance == null){
            instance = new RestaurantsManager();
        }
        return instance;
    }

    public void addRestaurant(Restaurant restaurant){
        restaurantList.add(restaurant);
    }

    public Restaurant getRestaurant(int i){
        return restaurantList.get(i);
    }
}
