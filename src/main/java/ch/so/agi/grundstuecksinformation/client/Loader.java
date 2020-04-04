package ch.so.agi.grundstuecksinformation.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class Loader {
    private static PopupPanel popup = new PopupPanel(false, true); // Create a modal dialog box that will not auto-hide

	public static void show(boolean isShow) {
		if (isShow) {
			FlowPanel div1 = new FlowPanel();
			div1.getElement().setClassName("lds-ripple");
			SimplePanel div2 = new SimplePanel();
			SimplePanel div3 = new SimplePanel();
			div1.add(div2);
			div1.add(div3);
			
            popup.add(div1);
            popup.setGlassEnabled(true); // Enable the glass panel  
            popup.center(); // Center the popup and make it visible 
		} else {
			popup.removeFromParent();
		}
	}
}
