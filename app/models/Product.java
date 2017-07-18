package models;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints.MaxLength;
import play.db.ebean.Model;

@Entity(name = "t_products")
public class Product extends Model {

    private interface DefaultView {
    }

    //          ATTRIBUTES
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({DefaultView.class})
    private Long id;

    @JsonView({DefaultView.class})
    private String name;

    @MaxLength(message = "500 caracteres como maximo para la descripcion", value = 500)
    @JsonView({DefaultView.class})
    private String description;

    //          DB OPERATIONS
    private static final Finder<Long, Product> finder = new Finder<>(Long.class, Product.class);

    void decreaseEachItemStockInAmount(Long amount) throws Exception {
        List<Item> items = Item.findAllByPropertie("product_id", this.getId());
        for (Item item : items) {
            item = Item.findByPropertie("id", item.getId());
            if (item.getStock() < amount) {
                throw new Exception("No se tiene stock suficiente del item: " + item.getId() + " para realizar la venta");
            } else {
                item.setStock(item.getStock() - amount);
                Item.update(item);
            }
        }
    }

    /**
     * This method search in the database all the Products.
     *
     * @return List<Product>
     * @throws Exception
     */
    public static List<Product> getList() throws Exception {
        return finder.all();
    }

    /**
     * This method create a Product in database.
     *
     * @param product
     * @throws Exception
     */
    public static void create(Product product) throws Exception {
        product.save();
    }

    /**
     * This method search in the database looking for an unique Product using the key and the corresponding
     * object given by the parameters.
     *
     * @param key
     * @param obj
     * @return Product
     * @throws java.lang.Exception
     */
    public static Product findByPropertie(String key, Object obj) throws Exception {
        return finder.where().eq(key, obj).findUnique();
    }

    /**
     * This method update a Product.
     *
     * @param product
     * @throws Exception
     */
    public static void update(Product product) throws Exception {
        product.update();
    }

    /**
     * This method delete a Product
     *
     * @param product
     * @throws Exception
     */
    public static void delete(Product product) throws Exception {
        product.delete();
    }

    //          SERIALIZATION
    /**
     * Serialize an eProduct with DefaultView annotated members.
     *
     * @return JsonNode
     * @throws Exception
     */
    public JsonNode jsonSerialization() throws Exception {
        return jsonSerialization(DefaultView.class);
    }

    /**
     * Serialize a Product with a particular set of attributes.
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
     * Serialize a list of ProductStates with all the annotated class members.
     *
     * @param products
     * @return
     * @throws Exception
     */
    public static JsonNode jsonListSerialization(List<Product> products) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<ObjectNode> list = new ArrayList();
        for (Product product : products) {
            list.add(mapper.convertValue(product.jsonSerialization(), ObjectNode.class));
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
