package panel;

import blocks.A;
import blocks.Block;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import javax.swing.JPanel;
import main.AnimationSquare;

public class LevelEditorPanel extends JPanel implements KeyEventDispatcher {
	
	private Block currentBlock;
	private BlockSelectBox[] selectArr = {};
	private BlockBox[][] editorArr;
	private int xOff, yOff, size;
	private boolean menuActive, shift, isLoading;
	private String name, errorMessage;
	
	public LevelEditorPanel() {
		this.currentBlock = null;
		this.editorArr = new BlockBox[40][90];
		this.menuActive = true;
		this.shift = false;
		this.name = "";
		this.errorMessage = "";
		
		size = 16;
		xOff = (1920 - size * editorArr[0].length) / 2;
		yOff = (1080 - size * editorArr.length) / 2 - 150;
		
		KeyboardFocusManager kf = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kf.addKeyEventDispatcher(this);
		
		for(int y = 0; y < editorArr.length; y++) {
			for(int x = 0; x < editorArr[0].length; x++) {
				editorArr[y][x] = new BlockBox(new Rectangle(xOff + (x * size),
					yOff + (y * size), size, size),
                    new A((new Rectangle(xOff + (x * size),
					yOff + (y * size), size, size)))) {
						public void doAction() {
							this.setBlock(currentBlock);
						}
						
						public void mouseDragged(MouseEvent me) {
							if(menuActive) return;
							super.mouseDragged(me);
						}
						
						public void mouseMoved(MouseEvent me) {
							if(menuActive) return;
							super.mouseMoved(me);
						}
						
						public void mouseClicked(MouseEvent me) {
							if(menuActive) return;
							super.mouseMoved(me);
						}
							 
					};
				this.addMouseListener(editorArr[y][x]);
				this.addMouseMotionListener(editorArr[y][x]);
			}
		}
		
		final LevelEditorPanel p = this;
		new Thread() {
			public void run() {
				while(isVisible()) {
					try {
						Thread.sleep(100);
					} catch(Exception ex) {}				
				}
				
				KeyboardFocusManager kf = KeyboardFocusManager.getCurrentKeyboardFocusManager();
				kf.removeKeyEventDispatcher(p);
			}
		}.start();
	}
 	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.DARK_GRAY);
		
		Graphics2D g2 = (Graphics2D)g;
				
		g.setColor(Color.GRAY);
		g.fillRect(xOff, yOff, 1920 - xOff * 2, 1080 - (yOff + 150) * 2);
		
		if(menuActive) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
			
            g.setColor(Color.LIGHT_GRAY);
		
            for(int y = 0; y < editorArr.length; y++) {
                for(int x = 0; x < editorArr[0].length; x++) {
                    g2.draw(editorArr[y][x].getRectangle());
                }
            }
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, 1920, 1080);
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g.setColor(Color.GRAY);
            
			g.drawString(name, 20, 20);
            g.fillRect(1920 / 2 - 250, 1080 / 2 - 300, 500, 600);
            
            return;
		}
		
		g.setColor(Color.LIGHT_GRAY);
		
		for(int y = 0; y < editorArr.length; y++) {
			for(int x = 0; x < editorArr[0].length; x++) {
				if(editorArr[y][x].mouseIsInside()) continue;
				g2.draw(editorArr[y][x].getRectangle());
			}
		}
		
		g.setColor(Color.YELLOW);
		
		for(int y = 0; y < editorArr.length; y++) {
			for(int x = 0; x < editorArr[0].length; x++) {
				if(!editorArr[y][x].mouseIsInside()) continue;
				g2.draw(editorArr[y][x].getRectangle());
			}
		}
		
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent ke) {        
		if(!menuActive || ke.getID() != KeyEvent.KEY_PRESSED) return false;
		
        if(ke.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			try {
                name = name.substring(0, name.length() - 1);
            } catch(Exception ex) {}
            return false;
        }
        
        if(name.length() == 16) return false;
        
		if(shift)
			shift = false;
		if(ke.getKeyCode() == KeyEvent.VK_SHIFT)
			shift = true;
        
		if(Character.isAlphabetic(ke.getKeyChar()) ||
                Character.isDigit(ke.getKeyChar())) {
			if(shift) {
				name += ke.getKeyChar() - 32;
				return false;
			}
			name += ke.getKeyChar();
        }
		
		return false;
	}
    
    public void save() throws Exception {
        File f = new File("level", name + ".txt");
        
        if(!f.exists()) {
            f.createNewFile();
        }
        
        FileWriter fw = new FileWriter(f);
        
        for(BlockBox[] arr : editorArr) {
            for(BlockBox box : arr) {
                fw.append(box.getBlock().getClass().getSimpleName());
            }
            System.out.println();
        }
    }
    
    public void load(String s) throws Exception {
        File f = new File("level", s + ".txt");
        
        if(!f.exists()) {
            errorMessage = "Level " + s + " does not exist.";
            return;
        }
        
        Scanner scan = new Scanner(f);
        
        while(scan.hasNextLine()) {
            
        }
    } 
        

	class BlockBox extends AnimationSquare {

        private Block b;
        
		public BlockBox(Rectangle r, Block b) {
            super(r);
            
            this.b = b;
        }
		
        public Block getBlock() {
            return b;
        }
        
        public void setBlock(Block b) {
            this.b = b;
        }
	}
    
    class BlockSelectBox extends AnimationSquare {

		private Block b;
		
		public BlockSelectBox(Rectangle r, Block b) {
			super(r);
			this.b = b;
		}
        
        public void doAction() {
            currentBlock = b;
        }
		
	}
}