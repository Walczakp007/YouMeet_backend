package youmeet.wpam.Contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import youmeet.wpam.DTO.UserSmallDTO;
import youmeet.wpam.Services.UserService;
import youmeet.wpam.exceptions.UserNotFoundException;

import javax.validation.Valid;

import static youmeet.wpam.config.utils.UtilsKeys.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/getAll")
    public ResponseEntity getAllPosts() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/getUserById")
    public ResponseEntity getUserById(@RequestParam(value = "id") Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (UserNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @PostMapping(value = "/api/updateUser")
    public ResponseEntity updateUser(@RequestBody UserSmallDTO dto) {
        return userService.updateUser(dto)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.badRequest()::build);
    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/getUserByEmail")
    public ResponseEntity getUserByEmail(@RequestParam(value = "email") String email) {
        try {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        } catch (UserNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @DeleteMapping(value = "/api/deleteUser")
    public ResponseEntity deleteUserById(@RequestParam(value = "id") Long id) {
        if (id != null) {
            userService.deleteUserById(id);
            return ResponseEntity.ok(HttpEntity.EMPTY);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/uploadToSThree")
    public ResponseEntity uploadToS3(@RequestParam(value = "id") Long id, @RequestParam(value = "url") String url) {
        try {
            return ResponseEntity.ok(userService.uploadToS3(url, id));
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/createUser")
    public ResponseEntity createUser(@Valid @RequestBody UserSmallDTO dto) {
        if (dto == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (userService.checkIfUserExistsByEmail(dto.getEmail())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok(userService.createUserBody(dto));
    }

    @GetMapping(value = "/generateToken")
    public ResponseEntity generateToken(@RequestParam(value = "email") String email) {
        try{
            return ResponseEntity.ok(userService.createTokenForUser(email));
        } catch (UserNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping(value = "/createFbUserAccount")
    public ResponseEntity createFbUserAccount(@Valid @RequestBody UserSmallDTO dto) {
        if (dto == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(userService.createFbUserAccount(dto));
    }
}
