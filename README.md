# Car Listing API


# Introduction
This Rest API provides endpoints to add, update and search for car listings.
You can add/update car listing by uploading CSV file or simply calling endpoint via JSON post.

The API is using in memory storage to store and retrieve information

# API Documentation
1. Upload csv file with car listings via post method ``/upload_csv/{dealer_id}``
2. Add car listing through JSON via post by using ``/vehicle_listing``
3. Search car listings by GET method  ``/search``
4. Search car listings with filters by GET method ``/search?model=a180&make=mercedes&year=2016``
5. Please check ``docs/Car-Listing.postman_collection.json`` postman collection for detailed API Specs
6. Please check ``docs/car_listing.csv`` for example csv format


# Steps to run the application from Console using Docker
1. Make sure you have Docker and JDK 16 installed in your local system
2. Run command to build an image 
   ```` 
   ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=heycar/car-listing    
   ````
3. Now run command to run dockerized image
```
docker run -p 8080:8080 docker.io/heycar/car-listing:latest
```

4. Now you can start consuming endpoints.

5. Application will be listening on port ```localhost:8080```


# Steps to run the application from IDEA
1. Just simply run main class ``CarListingApplication.java``
