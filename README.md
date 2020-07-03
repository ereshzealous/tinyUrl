# Tiny or Short URL in Spring boot

The requirement is to provide solution for tiny url creation and redirection. I have considered below things or addressing the solution.

- I have used in-memory database for demo. Constructed a simple entity and saved details in to that. 
- When a long url or any url is entered in to system it considers expiration for that link as well. For initial considerations I made it as 1 day. 
- If we try to access the short url after expiration it throws an error saying that "link has been expired".
- We can reactivate again the same link. 

## How short url/tiny url is calculated

- I created a simple rest API POST method which takes longUrl and expiry days as input request.
- Then these details are saved in in-memory database, I have used H2 here, some advance in-memory like redis also can be used. But for demo I have used H2. 
- After it is saved an auto generated ID. It is better that we create unique short key every time. 
- To do this it is combination of three.
-- Auto generated id.
-- A new class ID is used, it has only one member variable and initialized. After initialized with auto-generated id, hashcode is calculated.
-- Random UUID is used and extracted all numerics out of it and extracted first 5 digit.
-- The above generated random UID is taken and will find index position based on two logics, 1. percentile of UID length and each iteration divided by UID length. 
Please see below code snippet for more clarity.
```java
    public String generateShortUrl(Long id) {
        String UID = UUID.randomUUID().toString().replaceAll("-", "");
        ID idObject = new ID(id);
        String numbersOnly = UID.replaceAll("[^0-9]", "");
        numbersOnly = numbersOnly.length() > 5 ? StringUtils.substring(numbersOnly, 0, 5) : numbersOnly;
        id = idObject.hashCode() + id + Integer.parseInt(numbersOnly);
        StringBuilder key = new StringBuilder(32);
        while (id > 0) {
            Integer value = 0;
            Integer intValue = id.intValue();
            value = intValue < 0 ? 0 - intValue : intValue;
            key.insert(0, UID.charAt((value % UID.length())));
            id /= UID.length();
        }
        return key.toString();
    }
```
The above logic is used to calcuate shortcode for a long url after persisting in in-memory.

## Rest API

| API | Method | Request | Errors| Response |
| ------ | ------ | ------ | ------ | ------ |
| http://localhost:9000/ | POST | {"longUrl" : "http://google.co.in/", "expiryDays" : 10 | UnExpected Exception | Returns short url and other details. |
| http://localhost:9000/all | GET | NA | General Errors | Returns all available long and their corresponding short codes|
|http://localhost:9000/{code} | GET | NA | Not Found, Link expired, General Error | Will redirect to link if pasted in browser |

## Technology stack 
I have developed this using spring boot and in-memory database as H2. Junits as well. Even though I am comfortable writing this in goLang. But problem was asked in java.

# Further improvements 
- Implement authentication or authorization is provided. 
- Implement distributed cache for faster access to scale up to billions. 
- It can be deployed in any cloud server and generate the short url and can be used.
- Proper role maintainance to restrict access.
