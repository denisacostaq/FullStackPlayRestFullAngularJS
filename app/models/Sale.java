package models;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Min;
import play.db.ebean.Model;

@Entity
@Table(name = "t_sales")
public class Sale extends Model {

    private interface DefaultView {
    }

    public interface creation {
    }

    //          ATTRIBUTES
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({DefaultView.class})
    private Long id;

    @JsonView({DefaultView.class})
    @Constraints.Required(groups = creation.class)
    private String buyer;

    @Min(0)
    @JsonView({DefaultView.class})
    @Constraints.Required(groups = creation.class)
    private Long amount;

    @OneToOne
    @JsonView({DefaultView.class})
    @Constraints.Required(groups = creation.class)
    private Product product;

    //          DB OPERATIONS
    private static final Finder<Long, Sale> finder = new Finder<>(Long.class, Sale.class);

    /**
     * This method search in the database all the Sales.
     *
     * @return List<Sale>
     * @throws Exception
     */
    public static List<Sale> getList() throws Exception {
        return finder.all();
    }

    /**
     * This method create a Sale in database.
     *
     * @warning(aacostadeb@gmail.com): Call this in a transaction.
     * @param sale
     * @throws Exception
     */
    public static void create(Sale sale) throws Exception {
        Product product = Product.findByPropertie("id", sale.getProduct().getId());
        product.decreaseEachItemStockInAmount(sale.getAmount());
        sale.save();
    }

    /**
     * This method search in the database looking for an unique Sale using the key and the corresponding
     * object given by the parameters.
     *
     * @param key
     * @param obj
     * @return Sale
     * @throws java.lang.Exception
     */
    public static Sale findByPropertie(String key, Object obj) throws Exception {
        return finder.where().eq(key, obj).findUnique();
    }

    //          SERIALIZATION
    /**
     * Serialize an eSale with DefaultView annotated members.
     *
     * @return JsonNode
     * @throws Exception
     */
    public JsonNode jsonSerialization() throws Exception {
        return jsonSerialization(DefaultView.class);
    }

    /**
     * Serialize a Sale with a particular set of attributes.
     *
     * @param view
     * @return JsonNode
     * @throws IOException
     */
    public JsonNode jsonSerialization(Class view) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        ObjectWriter writer = mapper.writerWithView(view);
        JsonNode response = mapper.readTree(writer.writeValueAsString(this));
        return response;
    }

    /**
     * Serialize a list of SaleStates with all the annotated class members.
     *
     * @param sales
     * @return
     * @throws Exception
     */
    public static JsonNode jsonListSerialization(List<Sale> sales) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<ObjectNode> list = new ArrayList();
        for (Sale sale : sales) {
            list.add(mapper.convertValue(sale.jsonSerialization(), ObjectNode.class));
        }
        return mapper.convertValue(list, JsonNode.class);
    }

    //          GET/SET
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
