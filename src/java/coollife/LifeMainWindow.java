package coollife;

import firststep.Canvas;
import firststep.DoubleXY;
import firststep.Window;
import firststep.internal.GLFW;
import firststep.internal.NVG;
import firststep.internal.NVG.Color;

public class LifeMainWindow extends Window {
	
	private static final String APPNAME = "Cool Life";
	
	private static float fps = 25.0f;
	private int mouseI, mouseJ;
	private boolean isPaused;
	private boolean isDown;
	private boolean clickedColor;

	private Cells field;
	
	@Override
	public void key(int key, int scancode, int action, int mods) {
		if (key == GLFW.KEY_ESCAPE && action == GLFW.PRESS) {
			close();
		} else if (key == GLFW.KEY_SPACE && action == GLFW.PRESS) {
			isPaused = !isPaused;
	        updateTitle();
	    }
	}
	
	public void mouseButton(int button, int action, int mods) {
	    if (button == GLFW.MOUSE_BUTTON_LEFT && action == GLFW.PRESS)
	    {
	    	isDown = true;
	        if (!field.getCell(mouseI, mouseJ))
	        {
	        	field.setCell(mouseI, mouseJ, true);
	        	clickedColor = true;
	        }
	        else
	        {
	        	field.setCell(mouseI, mouseJ, false);
	        	clickedColor = false;
	        }
	    }
	    
	    if (button == GLFW.MOUSE_BUTTON_LEFT && action == GLFW.RELEASE) {
	    	isDown = false;
	    }
	}
	
	public void cursorPos(double x, double y) {
		if (isDown) {
        	field.setCell(mouseI, mouseJ, clickedColor);
		}
	}	
	private float calcScale(int winWidth, int winHeight)
	{
	    float wpw = (float)field.getWidth() / winWidth;
	    float hph = (float)field.getHeight() / winHeight;
	    float k = 1;
	    if (wpw > hph)
	    {
	        k = 1.0f / wpw;
	    }
	    else
	    {
	        k = 1.0f / hph;
	    }
	    return k;
	}

	private void cellUnderMouse(int winWidth, int winHeight)
	{
	    DoubleXY mPos = getCursorPos();

	    float k = calcScale(winWidth, winHeight);
	    float x0 = winWidth / 2 - (field.getWidth() * k / 2);
	    float y0 = winHeight / 2 - (field.getHeight() * k / 2);
	    
	    mouseI = (int) ((mPos.getX() - x0) / k);
	    mouseJ = (int) ((mPos.getY() - y0) / k);
	}
	
	private void updateTitle()
	{
	    if (isPaused)
	    {
	    	setTitle(APPNAME + " [ Pause ]");
	    }
	    else
	    {
	    	setTitle(APPNAME);
	    }
	}
	
	private long frameIndex = 0;
	
	@Override
	protected void frame(Canvas cnv) {
		float k = calcScale(getWidth(), getHeight());
	    
	    float x0 = getWidth() / 2 - (field.getWidth() * k / 2);
	    float y0 = getHeight() / 2 - (field.getHeight() * k / 2);
	    
	    // Painting dark cells
        cnv.beginPath();
        cnv.fillColor(new NVG.Color(0, 0, 0));
	    for (int i = 0; i < field.getWidth(); i++)
	    for (int j = 0; j < field.getHeight(); j++)
	    {
	        if (!field.getCell(i, j))
	        {
		        cnv.rect(x0 + i * k, y0 + j * k, k, k);
	        }
	    }
        cnv.fill();

	    // Painting light cells
        cnv.beginPath();
        cnv.fillColor(new NVG.Color(192, 192, 192));
	    for (int i = 0; i < field.getWidth(); i++)
	    for (int j = 0; j < field.getHeight(); j++)
	    {
	        if (field.getCell(i, j))
	        {
		        cnv.rect(x0 + i * k, y0 + j * k, k, k);
	        }
	    }
        cnv.fill();

	    
	    // Painting lines between cells
        cnv.beginPath();
        cnv.strokeColor(new NVG.Color(64, 64, 64, 255));
        if (k > 8)
        {
		    for (int i = 0; i < field.getWidth(); i++)
		    for (int j = 0; j < field.getHeight(); j++)
		    {
		        cnv.rect(x0 + i * k, y0 + j * k, k, k);
		    }
        }
        cnv.stroke();
	    
	    if (mouseI >= 0 && mouseI < field.getWidth() && mouseJ >= 0 && mouseJ < field.getHeight())
	    {
	        cnv.fillColor(new NVG.Color(255, 255, 255, 64));
	        cnv.beginPath();
	        cnv.rect(x0 + mouseI * k, y0 + mouseJ * k, k, k);
	        cnv.fill();
	    }

		if (!isPaused && frameIndex % 2 == 0) {
			field = LifeEngine.turn(field);
		}
		frameIndex ++;
		cellUnderMouse(getWidth(), getHeight());
	}

	
	public LifeMainWindow() {
		super (APPNAME, 600, 400);
		setBackground(new Color(0.2f, 0.2f, 0.2f, 1.0f));
		field = new Cells(100, 70);
	}
	
	public static void main(String... args) {
		LifeMainWindow mainWindow = new LifeMainWindow();
		
		Window.loop(fps);
	}
}