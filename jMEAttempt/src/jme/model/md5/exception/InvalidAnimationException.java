package jme.model.md5.exception;

/**
 * InvalidAnimationException is thrown when an invalid animation is being
 * loaded into the previously loaded skeletal system.
 * 
 * @author Yi Wang (Neakor)
 */
public class InvalidAnimationException extends RuntimeException{
	// Serial version.
	private static final long serialVersionUID = -7115839715066462208L;
	// Message.
	private static final String message = "Cannot load specified animation with loaded skeletal system.";

	/**
	 * Constructor of InvalidAnimationException.
	 */
	public InvalidAnimationException() {
		super(InvalidAnimationException.message);
	}
}
