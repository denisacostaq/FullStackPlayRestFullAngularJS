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

public class Item extends Controller {

    private static final Logger.ALogger apiLogger = Logger.of("apiInformation");

    public static Result list() {
        try {
            List<models.Item> items = models.Item.getList();
            JsonNode serialized = models.Item.jsonListSerialization(items);
            return ok(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Item.list.UnexpectedError"), e);
            return internalServerError();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        Form<models.Item> itemForm = Form.form(models.Item.class).bindFromRequest();
        if (itemForm.hasErrors()) {
            apiLogger.error(Messages.get("Item.create: " + itemForm.errorsAsJson()));
            return badRequest(itemForm.errorsAsJson());
        }
        try {
            models.Item item = itemForm.get();
            models.Item.create(item);
            JsonNode serialized = item.jsonSerialization();
            return created(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Item.create.UnexpectedError"), e);
            return internalServerError();
        }
    }

    public static Result retrive(Long id) {
        try {
            models.Item item = models.Item.findByPropertie("id", id);
            JsonNode serialized = item.jsonSerialization();
            return ok(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Item.retrive.UnexpectedError"), e);
            return internalServerError();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(Long id) {
        Form<models.Item> itemForm = Form.form(models.Item.class).bindFromRequest();
        if (itemForm.hasErrors()) {
            apiLogger.error(Messages.get("Item.update: " + itemForm.errorsAsJson()));
            return badRequest(itemForm.errorsAsJson());
        }
        try {
            models.Item item = itemForm.get();
            item.setId(id);
            models.Item.update(item);
            JsonNode serialized = item.jsonSerialization();
            return ok(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Item.update.UnexpectedError"), e);
            return internalServerError();
        }
    }

    public static Result delete(Long id) {
        Ebean.beginTransaction();
        try {
            models.Item item = models.Item.findByPropertie("id", id);
            item.setProduct(null);
            item.update();
            models.Item.delete(item);
            Ebean.commitTransaction();
            return ok();
        } catch (Exception e) {
            apiLogger.error(Messages.get("Item.delete.UnexpectedError"), e);
            return internalServerError();
        } finally {
            Ebean.endTransaction();
        }
    }

}
