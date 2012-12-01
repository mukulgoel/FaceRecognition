


import java.lang.*;
import java.io.*;
 
public class PPMFile implements xxxFile{


    private byte bytes[]=null;      // bytes which make up binary PPM image
    private double doubles[] = null;
    private String filename=null;     // filename for PPM image
    private int height = 0;
    private int width = 0;

   
    public PPMFile(String filename)  throws FileNotFoundException, IOException{
        this.filename = filename;
        readImage();
    }

    
    public int getHeight() {
      return height;
    }

   
    public int getWidth() {
      return width;
    }

    
    public byte[] getBytes() {
        return bytes;
    }
   
    public double[] getDouble() {
      return doubles;
    }

    
  public static void writeImage(String fn, byte[] data, int width, int height)
    throws FileNotFoundException, IOException {

        if (data != null) {

                FileOutputStream fos = new FileOutputStream(fn);
                fos.write(new String("P6\n").getBytes());
                fos.write(new String( width + " " + height + "\n").getBytes());
                fos.write(new String("255\n").getBytes());
                System.out.println(data.length);
                fos.write(data);
                fos.close();
          }
    }

    
    private void readImage()  throws FileNotFoundException, IOException, NumberFormatException {

        
           bytes=null;
            char buffer;                   
            String id = new String();       
            String dim = new String();     
            int count = 0;
            File f = new File(filename);
            FileInputStream isr = new FileInputStream(f);
            boolean weird = false;

            do {
                buffer = (char)isr.read();
                id = id + buffer;
                count ++;
            } while (buffer != '\n' && buffer != ' ');

            if (id.charAt(0) == 'P') {


                buffer = (char)isr.read();count ++;
                if (buffer == '#') {
                  do {
                    buffer = (char)isr.read();count ++;
                  } while (buffer != '\n');
                  count ++;
                  buffer = (char)isr.read();
                }
             
                do {
                    dim = dim + buffer;
                    buffer = (char)isr.read();count ++;
                } while (buffer != ' ' && buffer != '\n');

                width = Integer.parseInt(dim);
                 
                dim = new String();
                buffer = (char)isr.read();count ++;
                do {
                    dim = dim + buffer;
                    buffer = (char)isr.read();count ++;
                } while (buffer != ' ' && buffer != '\n');
                height = Integer.parseInt(dim);
                 
                do {                          
                    buffer = (char)isr.read();count ++;
                } while (buffer != ' ' && buffer != '\n');
  

                bytes = new byte[height*width];
                doubles = new double[height*width];

                
                if ((height*width + count*2) < f.length())
                  weird = true;

                if ((id.charAt(1) == '5') || (id.charAt(1) == '6')) {
                  if (!weird)
                    isr.read(bytes,0,height*width);
 
                  else {
                  
                    int v =0;
                    for (int i =0; i< height*width; i++) {
                      v = isr.read();
                      v = v + isr.read();
                      v = v + isr.read();
                      v = v / 3;
                      bytes[i] = (byte)( v & 0xFF);
                    }
                  }
                }
                if (id.charAt(1) == '2') {
                  int i = 0;
                  for (i =0; i < width*height;i++) {
                    dim = new String();
                    do {
                      buffer = (char)isr.read();
                      if (buffer != ' ' && buffer != '\n')
                        dim = dim +  buffer;
                    } while (buffer != ' ' && buffer != '\n');
                    bytes[i] = (byte)(Integer.parseInt(dim) & 0xFF);

                  }
                }
               
                for (int i=0; i < height*width;i++)
                    doubles[i] = (double)(bytes[i] & 0xFF);

                isr.close();
            }
            else {
                width = height = 0;
                doubles = new double[0]; bytes= new byte[0];
                throw new NumberFormatException("Wrong header information!");
            }
    }
}
