
package webbshop;

import java.util.List;
import java.util.Scanner;

public class Webbshop {

    public static void main(String[] args) {
        Repository repo = new Repository();
        Scanner scan = new Scanner(System.in);
        InputHandler handler = new InputHandler();
        
        String customer, product, order;
        while(true) {
            List<String> customerList = repo.printAndGetCustomers();
            System.out.println("Vem är du?");
            customer = scan.next();
            
            if (handler.formatCustomerChecker(customerList, customer))
                break;
        }
        
        System.out.println("");
        
        while(true) {
            List<String> productList = repo.printProducts();
            System.out.println("Välj en produkt att köpa.");
            product = scan.nextLine();
            
            if(handler.formatCustomerChecker(productList, product))
                break;
        }
        
        System.out.println("");
        
        while(true) {
            List<String> orderList = repo.showOrders(customer);
            System.out.println("Välj en order att lägga din valda produkt i.");
            order = scan.next();
            
            if((order = handler.formatOrderChecker(orderList, order)) != null)
                break;
        }
        
        System.out.println("");
        
        repo.AddProductToCart(customer, product, order);
    }

}
