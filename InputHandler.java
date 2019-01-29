
package webbshop;

import java.util.List;

public class InputHandler {
    public boolean formatCustomerChecker(List<String> customers, String input) {
        return customers.stream().anyMatch((s) -> (s.equals(input)));
    }
    
    public String formatOrderChecker(List<String> orders, String input) {
        for (String s : orders) {
            if (s.substring(1, 2).equals(input))
                return s.substring(4);
        }
        
        return null;
    }
}
