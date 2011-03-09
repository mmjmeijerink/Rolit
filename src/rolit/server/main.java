package rolit.server;

public class main {
	public static void main(String[] args) {
    	System.out.println("        Rolit Server door Mart Meijerink en Thijs Scheepers\n        v0.1alpha");
        ApplicationController appController = new ApplicationController();
        appController.view().log("        Rolit Server door Mart Meijerink en Thijs Scheepers\n         v0.1alpha");
    }
}
