package com.example.lux;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataRepository {

    private static final String PREFS_NAME = "UserPrefs"; // Name of the SharedPreferences file
    private static final String KEY_USERS_MAP = "usersMap"; // Key to store the user map

    private static volatile DataRepository instance; // volatile for thread safety
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson(); // For JSON serialization/deserialization

    private Map<String, User> users = new ConcurrentHashMap<>(); // email -> User (In-memory cache)
    private List<Room> rooms = new ArrayList<>();
    private List<Service> services = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Attraction> attractions = new ArrayList<>();

    private static final String URL_DELUXE = "https://images.unsplash.com/photo-1611892440504-42a792e24d32?q=80&w=2070&auto=format&fit=crop";
    private static final String URL_SUITE = "https://images.unsplash.com/photo-1618773928121-c32242e63f39?q=80&w=2070&auto=format&fit=crop";
    private static final String URL_STANDARD = "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?q=80&w=1770&auto=format&fit=crop";
    private static final String URL_SPA = "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?q=80&w=2070&auto=format&fit=crop";
    private static final String URL_DINING = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=1974&auto=format&fit=crop";
    private static final String URL_CABANA = "https://images.unsplash.com/photo-1584132967334-10e028bd69f7?q=80&w=2070&auto=format&fit=crop";
    private static final String URL_TOUR = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=2073&auto=format&fit=crop";
    private static final String URL_MARKET = "https://images.unsplash.com/photo-1533900298318-6b8da08a523e?q=80&w=2070&auto=format&fit=crop";
    private static final String URL_WATERSPORTS = "https://images.unsplash.com/photo-1605283176568-9b41fde3672e?q=80&w=1974&auto=format&fit=crop";
    private static final String URL_COCKTAILS = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?q=80&w=2157&auto=format&fit=crop";


    // Private constructor to prevent instantiation
    private DataRepository(Context context) {
        // Use application context to avoid memory leaks associated with Activity context
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadUsersFromPrefs(); // Load saved users first
        seedNonUserData();    // Seed other data (rooms, services etc.)
        // Optionally seed initial users only if storage is empty
        if (users.isEmpty()) {
            seedInitialUsers();
        }
    }

    public static DataRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (DataRepository.class) { // Double-checked locking for thread safety
                if (instance == null) {
                    instance = new DataRepository(context);
                }
            }
        }
        return instance;
    }

    private void loadUsersFromPrefs() {
        String usersJson = sharedPreferences.getString(KEY_USERS_MAP, null);
        if (usersJson != null) {
            Type type = new TypeToken<ConcurrentHashMap<String, User>>() {}.getType();
            try {
                users = gson.fromJson(usersJson, type);
                if (users == null) { // Handle case where JSON parsing might yield null
                    users = new ConcurrentHashMap<>();
                }
                Log.d("DataRepository", "Loaded " + users.size() + " users from SharedPreferences.");
            } catch (Exception e) {
                Log.e("DataRepository", "Error loading users from SharedPreferences", e);
                users = new ConcurrentHashMap<>(); // Initialize empty if error
            }
        } else {
            Log.d("DataRepository", "No users found in SharedPreferences.");
            users = new ConcurrentHashMap<>();
        }
    }

    private void saveUsersToPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String usersJson = gson.toJson(users);
        editor.putString(KEY_USERS_MAP, usersJson);
        editor.apply(); // Use apply() for asynchronous saving
        Log.d("DataRepository", "Saved " + users.size() + " users to SharedPreferences.");
    }

    private void seedInitialUsers() {
        Log.d("DataRepository", "Seeding initial users into SharedPreferences.");
        // Add initial users and save
        User user1 = new User("user1", "guest@example.com", "Alice Guest", "password123"); // HASH THIS PASSWORD!
        User user2 = new User("user2", "vip@example.com", "Bob VIP", "securepass");     // HASH THIS PASSWORD!
        users.put(user1.getEmail(), user1);
        users.put(user2.getEmail(), user2);
        saveUsersToPrefs(); // Save the initial users
    }

    private void seedNonUserData() {
        // Dummy Rooms
        rooms.clear(); // Clear existing before seeding
        rooms.add(new Room("room101", "Deluxe Room", "Comfortable room with garden view.", 250.0, URL_DELUXE, "Deluxe", true));
        rooms.add(new Room("room201", "Ocean View Suite", "Spacious suite with stunning ocean views and balcony.", 550.0, URL_SUITE, "Suite", true));
        rooms.add(new Room("room105", "Standard Room", "Basic amenities, cozy stay.", 180.0, URL_STANDARD, "Standard", false));

        // Dummy Services
        services.clear();
        services.add(new Service("spa01", "Relaxing Massage", "60-minute full body massage.", 120.0, URL_SPA));
        services.add(new Service("dine01", "Fine Dining Reservation", "Reserve a table at our 'Azure' restaurant.", 0.0, URL_DINING));
        services.add(new Service("cabana01", "Poolside Cabana", "Private cabana rental for the day.", 150.0, URL_CABANA));
        services.add(new Service("tour01", "Guided Beach Tour", "Explore the local coastline.", 50.0, URL_TOUR));

        // Dummy Attractions
        attractions.clear();
        attractions.add(new Attraction("Local Market", "Explore vibrant local crafts and food.", URL_MARKET));
        attractions.add(new Attraction("Water Sports Center", "Jet ski, parasailing, and more.", URL_WATERSPORTS));
        attractions.add(new Attraction("Hotel Exclusive: Sunset Cocktails", "Enjoy cocktails at our rooftop bar during sunset.", URL_COCKTAILS));

        // Dummy Bookings (These are still in-memory only for this example)
        bookings.clear();
        // Example: Add bookings if needed, perhaps linked to users loaded from prefs
        // if(users.containsKey("guest@example.com")) {
        //     bookings.add(new Booking("book1", users.get("guest@example.com").getUserId(), ...));
        // }
    }

    private void seedData() {
        // Dummy Users
        users.put("guest@example.com", new User("user1", "guest@example.com", "Alice Guest", "password123"));
        users.put("vip@example.com", new User("user2", "vip@example.com", "Bob VIP", "securepass"));

        // Dummy Rooms (with Image URLs)
        rooms.add(new Room("room101", "Deluxe Room", "Comfortable room with garden view.", 250.0, URL_DELUXE, "Deluxe", true));
        rooms.add(new Room("room201", "Ocean View Suite", "Spacious suite with stunning ocean views and balcony.", 550.0, URL_SUITE, "Suite", true));
        rooms.add(new Room("room105", "Standard Room", "Basic amenities, cozy stay.", 180.0, URL_STANDARD, "Standard", false));

        // Dummy Services (with Image URLs)
        services.add(new Service("spa01", "Relaxing Massage", "60-minute full body massage.", 120.0, URL_SPA));
        services.add(new Service("dine01", "Fine Dining Reservation", "Reserve a table at our 'Azure' restaurant.", 0.0, URL_DINING));
        services.add(new Service("cabana01", "Poolside Cabana", "Private cabana rental for the day.", 150.0, URL_CABANA));
        services.add(new Service("tour01", "Guided Beach Tour", "Explore the local coastline.", 50.0, URL_TOUR));

        // Dummy Bookings
        bookings.add(new Booking("book1", "user1", "room101", "Deluxe Room", "Room", new Date(), new Date(System.currentTimeMillis() + 86400000*2), "Confirmed"));
        bookings.add(new Booking("book2", "user2", "spa01", "Relaxing Massage", "Service", new Date(System.currentTimeMillis() + 86400000), null, "Confirmed"));

        // Dummy Attractions (with Image URLs)
        attractions.add(new Attraction("Local Market", "Explore vibrant local crafts and food.", URL_MARKET));
        attractions.add(new Attraction("Water Sports Center", "Jet ski, parasailing, and more.", URL_WATERSPORTS));
        attractions.add(new Attraction("Hotel Exclusive: Sunset Cocktails", "Enjoy cocktails at our rooftop bar during sunset.", URL_COCKTAILS));
    }

    // --- User Methods ---
    public User registerUser(String name, String email, String password) {
        // **SECURITY:** HASH the password here before storing!
        // String hashedPassword = hashPassword(password); // Implement this function

        if (users.containsKey(email)) {
            Log.w("DataRepository", "Registration failed: Email already exists - " + email);
            return null; // Email already exists
        }

        String userId = "user" + UUID.randomUUID().toString().substring(0, 6); // More unique ID
        // Store the plain password here for DEMO ONLY. Store HASHED password in real app.
        User newUser = new User(userId, email, name, password);
        users.put(email, newUser); // Add to in-memory cache
        saveUsersToPrefs();        // Persist the updated user map
        Log.i("DataRepository", "User registered successfully: " + email);
        return newUser;
    }

    public User loginUser(String email, String password) {
        User user = users.get(email); // Check in-memory cache first

        if (user != null) {
            // **SECURITY:** Retrieve stored hash and compare with hash of entered password
            // if (verifyPassword(password, user.getStoredPasswordHash())) { ... }

            // DEMO ONLY: Comparing plain text passwords (INSECURE)
            if (user.getPassword().equals(password)) {
                Log.i("DataRepository", "Login successful for: " + email);
                return user;
            } else {
                Log.w("DataRepository", "Login failed: Incorrect password for " + email);
                return null; // Incorrect password
            }
        } else {
            Log.w("DataRepository", "Login failed: User not found - " + email);
            return null; // User not found
        }
    }


    public User getUserById(String userId) {
        for (User user : users.values()) { // Iterate through the values of the map
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }


    // --- Room Methods ---
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms); // Return a copy
    }

    public Room getRoomById(String roomId) {
        for (Room room : rooms) {
            if (room.getRoomId().equals(roomId)) {
                return room;
            }
        }
        return null;
    }

    // --- Service Methods ---
    public List<Service> getAllServices() {
        return new ArrayList<>(services); // Return a copy
    }

    public Service getServiceById(String serviceId) {
        for (Service service : services) {
            if (service.getServiceId().equals(serviceId)) {
                return service;
            }
        }
        return null;
    }

    // --- Booking Methods ---
    public List<Booking> getBookingsByUserId(String userId) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            // Only return active bookings for the specified user
            if (booking.getUserId().equals(userId) && !"Cancelled".equals(booking.getStatus())) {
                userBookings.add(booking);
            }
        }
        return userBookings;
    }

    public Booking createBooking(String userId, String itemId, String itemName, String itemType, Date startDate, Date endDate) {
        // Basic check if item exists (room or service) - more robust checks needed in real app
        boolean itemExists = false;
        if ("Room".equals(itemType)) {
            itemExists = getRoomById(itemId) != null;
        } else if ("Service".equals(itemType)) {
            itemExists = getServiceById(itemId) != null;
        }

        if (!itemExists || getUserById(userId) == null) {
            return null; // Invalid user or item
        }

        String bookingId = "book" + UUID.randomUUID().toString().substring(0, 8); // Generate unique ID
        Booking newBooking = new Booking(bookingId, userId, itemId, itemName, itemType, startDate, endDate, "Confirmed");
        bookings.add(newBooking);
        return newBooking;
    }

    public boolean cancelBooking(String bookingId, String userId) {
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            // Ensure the user owns this booking before cancelling
            if (booking.getBookingId().equals(bookingId) && booking.getUserId().equals(userId)) {
                // Option 1: Remove the booking entirely
                bookings.remove(i);
                return true;
                // Option 2: Mark as cancelled (better for history)
                // booking.setStatus("Cancelled");
                // return true;
            }
        }
        return false; // Booking not found or user mismatch
    }

    // --- Attraction Methods ---
    public List<Attraction> getAllAttractions() {
        return new ArrayList<>(attractions);
    }
}