package models;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.io.IOException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.db.ebean.Model;
import play.data.validation.Constraints.MaxLength;

@Entity
@Table(name = "t_products")
public class Product extends Model {

    public interface DefaultView {
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

    @JsonView({DefaultView.class})
    private List<Item> items;

    //          DB OPERATIONS
    private static final Finder<Long, Product> finder = new Finder<>(Long.class, Product.class);

    void decreaseEachItemStockInAmount(Long amount) throws Exception {
        List<Item> its = Item.findItemsByProductId(this.getId());
        for (Item item : its) {
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
     *
     * @param items
     * @param id
     * @return An element from items with the same id value in the parameter, null if not found.
     */
    private static boolean hasElementFromListById(List<Item> items, Long id) {
        if (items != null && !items.isEmpty()) {
            for (Item item : items) {
                if (Objects.equals(item.getId(), id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add incoming items not present in database and remove no incoming items present in database.
     *
     * @throws Exception
     */
    private void syncItems() throws Exception {
        List<Item> retrivedItems = Item.findItemsByProductId(this.getId());
        if (this.getItems() != null && !this.getItems().isEmpty()) {
            for (Item item : this.getItems()) {
                item = models.Item.findByPropertie("id", item.getId());
                if (!hasElementFromListById(retrivedItems, item.getId())) {
                    List<models.Product> products = item.getProducts();
                    // add inconming
                    products.add(this);
                    item.setProducts(products);
                    models.Item.update(item);
                }
            }
        }
        for (Item item : retrivedItems) {
            item = models.Item.findByPropertie("id", item.getId());
            if (!hasElementFromListById(this.getItems(), item.getId())) {
                List<models.Product> products = item.getProducts();
                // remove no inconming
                products.remove(this);
                item.setProducts(products);
                models.Item.update(item);
            }
        }
    }

    private void unbindItems() throws Exception {
        SqlUpdate down = Ebean.createSqlUpdate("DELETE from `item_product` WHERE `product_id` = :product_id;");
        down.setParameter("product_id", this.getId());
        down.execute();
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
     * @warning(aacostadeb@gmail.com): Call this in a transaction.
     * @param product
     * @throws Exception
     */
    public static void create(Product product) throws Exception {
        product.save();
        product.syncItems();
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
     * @warning(aacostadeb@gmail.com): Call this in a transaction.
     * @param product
     * @throws Exception
     */
    public static void update(Product product) throws Exception {
        product.update();
        product.syncItems();
    }

    /**
     * This method delete a Product
     *
     * @warning(aacostadeb@gmail.com): Call this in a transaction.
     * @param product
     * @throws Exception
     */
    public static void delete(Product product) throws Exception {
        product.unbindItems();
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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
