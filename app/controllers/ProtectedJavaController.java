package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import be.objectify.deadbolt.java.actions.SubjectPresent;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Example of a protected controller.
 *
 * Created by lglossman on 11/7/16.
 */
public class ProtectedJavaController extends Controller {

    @SubjectPresent
    public Result tokenPresent() {
        return ok();
    }

    @Restrict(@Group("posts"))
    public Result singleScope() {
        return ok();
    }

}
