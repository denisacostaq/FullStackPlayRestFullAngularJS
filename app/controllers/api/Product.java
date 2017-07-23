package controllers.api;

import java.util.List;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.BodyParser;
import play.i18n.Messages;
import play.data.Form;

public class Product extends Controller {

    private static final Logger.ALogger apiLogger = Logger.of("apiInformation");

    public static Result list() {
        try {
            List<models.Product> products = models.Product.getList();
            JsonNode serialized = models.Product.jsonListSerialization(products);
            return ok(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Product.list.UnexpectedError"), e);
            return internalServerError();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        Form<models.Product> productForm = Form.form(models.Product.class).bindFromRequest();
        if (productForm.hasErrors()) {
            apiLogger.error(Messages.get("Product.create: " + productForm.errorsAsJson()));
            return badRequest(productForm.errorsAsJson());
        }
        // NOTE(aacostadeb@gmail.com): the create operation on Product is performed in multiple database steps
        // internaly, so `beginTransaction()`.
        Ebean.beginTransaction();
        try {
            models.Product product = productForm.get();
            models.Product.create(product);
            Ebean.commitTransaction();
            JsonNode serialized = product.jsonSerialization();
            return created(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Product.create.UnexpectedError"), e);
            return internalServerError();
        } finally {
            Ebean.endTransaction();
        }
    }

    public static Result retrive(Long id) {
        try {
            models.Product product = models.Product.findByPropertie("id", id);
            JsonNode serialized = product.jsonSerialization();
            return ok(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Product.retrive.UnexpectedError"), e);
            return internalServerError();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(Long id) {
        Form<models.Product> productForm = Form.form(models.Product.class).bindFromRequest();
        if (productForm.hasErrors()) {
            apiLogger.error(Messages.get("Product.update: " + productForm.errorsAsJson()));
            return badRequest(productForm.errorsAsJson());
        }
        // NOTE(aacostadeb@gmail.com): the update operation on Product is performed in multiple database steps
        // internaly, so `beginTransaction()`.
        Ebean.beginTransaction();
        try {
            models.Product product = productForm.get();
            product.setId(id);
            models.Product.update(product);
            Ebean.commitTransaction();
            JsonNode serialized = product.jsonSerialization();
            return ok(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Product.update.UnexpectedError"), e);
            return internalServerError();
        } finally {
            Ebean.endTransaction();
        }
    }

    public static Result delete(Long id) {
        Ebean.beginTransaction();
        try {
            models.Product product = models.Product.findByPropertie("id", id);
            product.setItems(null);
            models.Product.update(product);
            models.Product.delete(product);
            Ebean.commitTransaction();
            return ok();
        } catch (Exception e) {
            apiLogger.error(Messages.get("Product.delete.UnexpectedError"), e);
            return internalServerError();
        } finally {
            Ebean.endTransaction();
        }
    }

}
