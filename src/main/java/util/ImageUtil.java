package util;
import java.awt.Image;
import javax.swing.ImageIcon;
public class ImageUtil { public static ImageIcon resize(ImageIcon icon, int w, int h){ if(icon==null)return null; Image img=icon.getImage().getScaledInstance(w,h,Image.SCALE_SMOOTH); return new ImageIcon(img);} }
