# stock-api

A. Initialization : 
- run this commande to download Posgres Docker image : docker run --name stock-db -e POSTGRES_DB=stock -e POSTGRES_USER=stock -e POSTGRES_PASSWORD=stock -p 5433:5432 -d postgres:16

- if you add data jpa stater, you got to add your migration sql file into /resources/db/migration before starting the project and the database properties in the application.yml file

B. Developpement : 
- you can choose more than one profil such as DEV or PROD pofile. for that, you got to anotate the service class depending on the profil you want with the @profil anotation. Then add in application.yml the profile you want to use either for DEV or PROD like : 

  profiles:
    active: prod
- you can read application.yaml properties by an anotated class with @ConfigurationProperties. By separating application-dev.yaml from application-prod.yaml or other profile else, and set the profile you want in appilcatoin.yaml, you can read what you want in yours beans. Then for run your program depending on an profile, you have to choose one of them mehodes : 
Comment activer un profil (au choix) :

1.by default, in application.yml (dev session) :
yaml
   spring:
     profiles:
       active: dev

2.in IntelliJ, without modify principal yaml file : Run > Edit Configurations, then in Program arguments : --spring.profiles.active=prod. if this field is not visible, click on Modify options and active program arguments.
3.With Maven, in PowerShell keep the "" :
  * .\mvnw spring-boot:run "-Dspring-boot.run.profiles=prod"
3.With environnement variable : SPRING_PROFILES_ACTIVE=prod.

- @AllArgsConstructor : with this anotation on a @Entity class, no need to set default constructor. just add the constructor you want in your bean

- @NoArgsConstructor : beacause we have defined explicite constructors in our @entity object class, and thad @Data from Lombok doesn't create automatically the default constructor, we got to add this anotation on the @entity to avoid default constructor creation.

B1. Exceptions
apart from the natives exceptions like runtime exception, IOException that we could standardize as internal error, regarding business error we got to create a Global Exception Handler which will extends the ResponseEntityExceptionHandler class. In addition to centrilizing error case management, this also avoids rewriting. So : 
- First we write ours specifics business exceptions. In our case for example : 
   * ResourceNotFoundException extends RuntimeException
   * DuplicateCategorieException extends RuntimeException
- Then we write the Global Exception : 
   * GlobalExceptionHandler extends ResponseEntityExceptionHandler : ResponseEntityExceptionHandler is an abstract class which override : 
      ** ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request); 
        
        handleMethodArgumentNotValid is used for post error cases. 
      
      **  @ExceptionHandler(DuplicateCategorieException.class)
          public ProblemDetail handleDuplicateSku(DuplicateCategorieException ex) {
                ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
                problem.setTitle("Conflit de categorie");
                return problem;
         }

         with @ExceptionHandler anotation we can write some methode which handle our specifics business exceptions which are managed by th ProblemDetail class and returned to the client properly instead of native html illegible
So in any controller we throw a specific business exception define in the @RestControllerAdvice, this class will handle it and returned a managed response. 

