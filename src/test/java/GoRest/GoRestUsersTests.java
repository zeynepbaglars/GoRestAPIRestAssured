package GoRest;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GoRestUsersTests {

    int userID;
    Faker faker = new Faker();

    RequestSpecification reqSpec;

    @BeforeClass
    public void setup(){
        baseURI = "https://gorest.co.in/public/v2/users";

        reqSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer b75ed5da83d8499b54a4e02d0f2bb030b3f80982d499947e6a5c112ee183487c")
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test(priority = 1)
    public void createUserMap() {

        System.out.println("baseURI = " + baseURI);
        String rndFullname = faker.name().fullName();
        String rndEmail = faker.internet().emailAddress();

        Map<String,String> newUser=new HashMap<>();
        newUser.put("name",rndFullname);
        newUser.put("gender","male");
        newUser.put("email",rndEmail);
        newUser.put("status","active");

        userID =
                given()
                        .spec(reqSpec)
                        .body(newUser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }

    @Test(enabled = false,priority = 1)
    public void createUserClass() {
        String rndFullname = faker.name().fullName();
        String rndEmail = faker.internet().emailAddress();

        User newUser=new User();
        newUser.name=rndFullname;
        newUser.gender="male";
        newUser.email=rndEmail;
        newUser.status="active";

        userID =
                given()
                        .spec(reqSpec)
                        .body(newUser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }

    @Test(dependsOnMethods = {"createUserMap"},priority = 2)
    public void getUserByID(){

        given()
                .spec(reqSpec)

                .when()
                .get(""+userID)

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(userID));
    }

    @Test(dependsOnMethods = {"createUserMap"},priority = 3)
    public void updateUser(){

        String name=faker.name().fullName();

        Map<String,String> updateUser=new HashMap<>();
        updateUser.put("name",name);

        given()
                .spec(reqSpec)
                .body(updateUser)

                .when()
                .put(""+userID)

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(userID))
                .body("name",equalTo(name));
    }

    @Test (dependsOnMethods ={"updateUser"}, priority = 4)
    public void deleteUser(){
        given()
                .spec(reqSpec)

                .when()
                .delete(""+userID)

                .then()
                .statusCode(204);
    }

    @Test(dependsOnMethods = {"deleteUser"},priority = 5)
    public void deleteUserNegative(){
        given()
                .spec(reqSpec)

                .when()
                .delete(""+userID)

                .then()
                .statusCode(404);

    }
}
