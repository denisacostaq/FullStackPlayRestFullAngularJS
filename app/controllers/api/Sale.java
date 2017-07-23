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

public class Sale extends Controller {

    private static final Logger.ALogger apiLogger = Logger.of("apiInformation");

    public static Result list() {
        try {
            List<models.Sale> sales = models.Sale.getList();
            JsonNode serialized = models.Sale.jsonListSerialization(sales);
            return ok(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Sale.list.UnexpectedError"), e);
            return internalServerError();
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() {
        Form<models.Sale> saleForm = Form.form(models.Sale.class, models.Sale.creation.class).bindFromRequest();
        if (saleForm.hasErrors()) {
            apiLogger.error(Messages.get("Sale.create: " + saleForm.errorsAsJson()));
            return badRequest(saleForm.errorsAsJson());
        }
        // NOTE(aacostadeb@gmail.com): the create operation on Sale is performed in multiple database steps
        // internaly, so `beginTransaction()`.
        Ebean.beginTransaction();
        try {
            models.Sale sale = saleForm.get();
            models.Sale.create(sale);
            Ebean.commitTransaction();
            JsonNode serialized = sale.jsonSerialization();
            return created(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Sale.create.UnexpectedError"), e);
            return internalServerError();
        } finally {
            Ebean.endTransaction();
        }
    }

    public static Result retrive(Long id) {
        try {
            models.Sale sale = models.Sale.findByPropertie("id", id);
            JsonNode serialized = sale.jsonSerialization();
            return ok(serialized);
        } catch (Exception e) {
            apiLogger.error(Messages.get("Sale.retrive.UnexpectedError"), e);
            return internalServerError();
        }
    }

}
