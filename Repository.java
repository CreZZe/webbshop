
package webbshop;

import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Repository {
    Connection con;
    
    private void setupDatabaseConnection() {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("src/webbshop/Settings.properties"));
            
            // Manually loading the Java Database Connection driver.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            
            con = DriverManager.getConnection(p.getProperty("connectionString"), 
                                              p.getProperty("name"), 
                                              p.getProperty("password"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void closeDatabaseConnection() {
        try {
            if (con != null)
                con.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<String> printAndGetCustomers() {
        setupDatabaseConnection();
        System.out.println("Printing customers..");
        
        List<String> customerList = new ArrayList<>();
        
        try {
            PreparedStatement customersStatement = con.prepareStatement("SELECT name FROM customer");
            ResultSet customersResult = customersStatement.executeQuery();
            
            while (customersResult.next()) {
                String name = customersResult.getString("name");
                System.out.println(name);
                customerList.add(name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeDatabaseConnection();
        }            
        return customerList;
    }
    
    public List<String> printProducts() {
        setupDatabaseConnection();
        System.out.println("Printing products..");
        
        List<String> productList = new ArrayList<>();
        
        try {
            PreparedStatement productsStatement = con.prepareStatement("SELECT name, price, shoeSize, color, quantityInStore FROM shoes");
            ResultSet productsResult = productsStatement.executeQuery();
            
            while (productsResult.next()) {
                System.out.println(productsResult.getString("name") + " | Pris: "
                                                + productsResult.getString("price") + " | Storlek: "
                                                + productsResult.getString("shoeSize") + " | Utseende: "
                                                + productsResult.getString("color") + " | Lager: "
                                                + productsResult.getString("quantityInStore"));
                productList.add(productsResult.getString("name"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeDatabaseConnection();
        }
        return productList;
    }
    
    public List<String> showOrders(String custName) {
        setupDatabaseConnection();
        
        List<String> orderList = new ArrayList<>();
        String nyOrder = "Ny Order";
        int count = 1;
        
        
        try {
            PreparedStatement ordersStatement = con.prepareStatement("SELECT orderDate FROM orders INNER JOIN customer ON customer.id = orders.customerID AND customer.name = ? AND orders.sent = 0");
            ordersStatement.setString(1, custName);
            ResultSet ordersResult = ordersStatement.executeQuery();
            
            while(ordersResult.next()) {
                String order = ordersResult.getString("orderDate");
                orderList.add("[" + count + "] " + order);
                count++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            orderList.add("[" + count + "] " + nyOrder);
            
            for(String s : orderList) {
                System.out.println(s);
            }
            
            closeDatabaseConnection();
        }   
        return orderList;
    }
    
    public void AddProductToCart(String customer, String product, String order) {
        setupDatabaseConnection();
        
        try {
            if (order.equals("Ny order"))
                order = null;
            
            CallableStatement addToCartStatement = con.prepareCall("{CALL AddToCart(?, ?, ?)}");
            addToCartStatement.setInt(1, getCustomerID(customer));
            addToCartStatement.setInt(2, getOrderID(order));
            addToCartStatement.setInt(3, getProductID(product));            
            addToCartStatement.execute();
            
            ResultSet addToCartResult = addToCartStatement.getResultSet();
                        
            if (addToCartResult != null) {
                while (addToCartResult.next()) {
                    System.out.println(addToCartResult.getString("error"));
                    System.out.println("Detta felmeddelande kan bero på ett tomt lagersaldo!");
                }    
            }
            else {
                System.out.println(product + " har blivit tillagd till order [" + order + "] för kund " + customer);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeDatabaseConnection();
        }
    }
    
    private int getCustomerID(String customer) {
        int id = 0;
        
        try {
            PreparedStatement custIDStatement = con.prepareStatement("SELECT id FROM customer WHERE name = ?");
            custIDStatement.setString(1, customer);
            ResultSet custIDResult = custIDStatement.executeQuery();

            while(custIDResult.next()) {
                id = Integer.parseInt(custIDResult.getString("id"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    private int getOrderID(String order) {
        int id = 0;
        
        try {
            PreparedStatement orderIDStatement = con.prepareStatement("SELECT id FROM orders WHERE orderDate = ?");
            orderIDStatement.setString(1, order);
            ResultSet orderIDResult = orderIDStatement.executeQuery();
        
            while(orderIDResult.next()) {
                id = Integer.parseInt(orderIDResult.getString("id"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    private int getProductID(String product) {
        int id = 0;
        
        try {
            PreparedStatement prodIDStatement = con.prepareStatement("SELECT id FROM shoes WHERE name = ?");
            prodIDStatement.setString(1, product);
            ResultSet prodIDResult = prodIDStatement.executeQuery();

            while(prodIDResult.next()) {
                id = Integer.parseInt(prodIDResult.getString("id"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}
