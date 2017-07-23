package models;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.Constraints.Min;
import play.db.ebean.Model;

@Entity(name = "t_items")
public class Item extends Model {

    private interface DefaultView {
    }

    //          ATTRIBUTES
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({DefaultView.class})
    private Long id;

    @JsonView({DefaultView.class})
    private String name;

    @Min(0)
    @JsonView({DefaultView.class})
    private Long stock;

    @ManyToMany
    @JoinTable(name = "item_product",
            joinColumns = {
                @JoinColumn(name = "item_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "product_id")})
    private List<Product> products;

    //          DB OPERATIONS
    private static final Finder<Long, Item> finder = new Finder<>(Long.class, Item.class);

    /**
     * This method search in the database all the Items.
     *
     * @return List<Item>
     * @throws Exception
     */
    public static List<Item> getList() throws Exception {
        return finder.all();
    }

    /**
     * This method create an Item in database.
     *
     * @param item
     * @throws Exception
     */
    public static void create(Item item) throws Exception {
        item.save();
    }

    /**
     * This method search in the database looking for an unique Item using the key and the corresponding
     * object given by the parameters.
     *
     * @param key
     * @param obj
     * @return Item
     * @throws java.lang.Exception
     */
    public static Item findByPropertie(String key, Object obj) throws Exception {
        return finder.where().eq(key, obj).findUnique();
    }

    /**
     * This method search in the database looking for all Items using the key and the corresponding object
     * given by the parameters.
     *
     * @param key
     * @param obj
     * @return List<Item>
     * @throws java.lang.Exception
     */
    public static List<Item> findAllByPropertie(String key, Object obj) throws Exception {
        return finder.where().eq(key, obj).findList();
    }

    /**
     * This method search in the database looking for all Items in a product.
     *
     * @param productId
     * @return List<Item>
     * @throws java.lang.Exception
     */
    public static List<Item> findItemsByProductId(Long productId) throws Exception {
        return finder.fetch("products").where().eq("product_id", productId).findList();
    }

    /**
     * This method update an Item.
     *
     * @param item
     * @throws Exception
     */
    public static void update(Item item) throws Exception {
        item.update();
    }

    /**
     * This method delete an Item
     *
     * @param item
     * @throws Exception
     */
    public static void delete(Item item) throws Exception {
        item.delete();
    }

    //          SERIALIZATION
    /**
     * Serialize an eItem with DefaultView annotated members.
     *
     * @return JsonNode
     * @throws Exception
     */
    public JsonNode jsonSerialization() throws Exception {
        return jsonSerialization(DefaultView.class);
    }

    /**
     * Serialize an Item with a particular set of attributes.
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
     * Serialize a list of ItemStates with all the annotated class members.
     *
     * @param items
     * @return
     * @throws Exception
     */
    public static JsonNode jsonListSerialization(List<Item> items) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<ObjectNode> list = new ArrayList();
        for (Item item : items) {
            list.add(mapper.convertValue(item.jsonSerialization(), ObjectNode.class));
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

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
