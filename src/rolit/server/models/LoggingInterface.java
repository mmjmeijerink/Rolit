package rolit.server.models;
/**
 * De logging interface geeft aan of een object kan loggen of niet, als een object kan loggen kan dit bijvoorbeeld naar de UI of naar de Terminal.
 * Meestal is de ApplicationController een LoggingInterface
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public interface LoggingInterface {
	/**
	 * Als je iets te loggen hebt kan je dat via deze methode doen.
	 * @param logEntry de te loggen string
	 */
	public void log(String logEntry);
}