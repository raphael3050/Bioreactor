package servPattern;
import java.io.InputStream;
import java.io.OutputStream;

public interface IProtocole {

	void execute( IContext aContext , InputStream anInputStream , OutputStream anOutputStream );
	
}
