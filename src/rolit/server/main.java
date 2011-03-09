package rolit.server;

public class main {
	public static void main(String[] args) {
        ApplicationController appController = new ApplicationController();
        appController.view().log(	"        Rolit Server door Mart Meijerink en Thijs Scheepers" +
        							"\n         v0.1alpha" +
        							"\n         Protocol: Rolit2011Inf2");
    }
}
