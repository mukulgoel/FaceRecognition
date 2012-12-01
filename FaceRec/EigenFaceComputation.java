



import Jama.*;

public class EigenFaceComputation {

  
  private final static int MAGIC_NR = 11;

  public static FaceBundle submit(double[][] face_v, int width, int height, String[] id) {

    int length = width*height;
    int nrfaces = face_v.length;
    int i, j, col,rows, pix, image;
    double temp = 0.0;
    double[][] faces = new double[nrfaces][length];

    
    ImageFileViewer simple = new ImageFileViewer();
   simple.setImage(face_v[0],width,height);

    double[] avgF = new double[length];

    for ( pix = 0; pix < length; pix++) {
      temp = 0;
      for ( image = 0; image < nrfaces; image++) {
        temp +=  face_v[image][pix];
      }
      avgF[pix] = temp / nrfaces;
    }

    simple.setImage(avgF, width,height);

    for ( image = 0; image < nrfaces; image++) {

      for ( pix = 0; pix < length; pix++) {
        face_v[image][pix] = face_v[image][pix] - avgF[pix];
      }
    }
   
    System.arraycopy(face_v,0,faces,0,face_v.length);

    simple.setImage(face_v[0],width,height);

    /*
     Build covariance matrix. MxM
    */

    Matrix faceM = new Matrix(face_v, nrfaces,length);

/*for(i = 0; i < height; i++)
for(j = 0; j < width; j++)
System.out.print("matrix = ", faceM);*/



    Matrix faceM_transpose = faceM.transpose();


double[][] valsTransposed = faceM_transpose.getArray();           





    /*
     Covariance matrix - its MxM (nrfaces x nrfaces)
     */
    Matrix covarM = faceM.times(faceM_transpose);

    double[][] z = covarM.getArray();
    System.out.println("Covariance matrix is "+z.length+" x "+z[0].length);    //printing size of covarinace matrix

    /*
     Compute eigenvalues and eigenvector. Both are MxM
    */
    EigenvalueDecomposition E = covarM.eig();

    double[] eigValue = diag(E.getD().getArray());
    double[][] eigVector = E.getV().getArray();
int k;
System.out.println("eigen values");
for(i = 0; i < eigValue.length ; i++)    // displaying eigen values
System.out.println(eigValue[i]);


System.out.println("eigen vectors");          // displaying eigen vectors
for(i = 0; i < eigVector.length ; i++)
{
System.out.println("\n");
for(k = 0; k < eigVector[i].length ; k++)
System.out.print(eigVector[i][k]+" ");

}
    
    int[] index = new int[nrfaces];
    double[][] tempVector = new double[nrfaces][nrfaces];  /* Temporary new eigVector */

    for ( i = 0; i <nrfaces; i++) 
      index[i] = i;

    doubleQuickSort(eigValue, index,0,nrfaces-1);

    // Put the index in inverse
    int[] tempV = new int[nrfaces];
    for ( j = 0; j < nrfaces; j++)
      tempV[nrfaces-1-j] = index[j];
    
    /*for (j = 0; j< nrfaces; j++) {
      System.out.println(temp[j]+" (was: "+index[j]+") "+eigValue[temp[j]]);
    }*/
    
    index = tempV;

     /*
      * Put the sorted eigenvalues in the appropiate columns.
     */
    for ( col = nrfaces-1; col >= 0; col --) {
      for ( rows = 0; rows < nrfaces; rows++ ){
        tempVector[rows][col] = eigVector[rows][index[col]];
      }
    }
    eigVector = tempVector;
System.out.println("new sorted eigen vectors");          // displaying eigen vectors
for(i = 0; i < eigVector.length ; i++)
{
System.out.println("\n");
for(k = 0; k < eigVector[i].length ; k++)
System.out.print(eigVector[i][k]+" ");

}
    tempVector = null;
    eigValue = null;
    
     Matrix eigVectorM = new Matrix(eigVector, nrfaces,nrfaces);
     eigVector = eigVectorM.times(faceM).getArray();


 

     for ( image = 0; image < nrfaces; image++) {
      temp = max(eigVector[image]); // Our max
      for ( pix = 0; pix < eigVector[0].length; pix++)
       // Normalize
        eigVector[image][pix] = Math.abs( eigVector[image][pix] / temp);
    }



    

    double[][] wk = new double[nrfaces][MAGIC_NR]; // M rows, 11 columns

    /*
     Compute our wk.
    */

    for (image = 0; image < nrfaces; image++) {
      for (j  = 0; j <  MAGIC_NR; j++) {
        temp = 0.0;
        for ( pix=0; pix< length; pix++)
          temp += eigVector[j][pix] * faces[image][pix];
        wk[image][j] = Math.abs( temp );
      }
    }

System.out.println("\nface space");          // displaying eigen vectors
for(i = 0; i < wk.length ; i++)
{
System.out.println("\n");
for(k = 0; k < wk[i].length ; k++)
System.out.print(wk[i][k]+" ");

}



    FaceBundle b = new FaceBundle(avgF, wk, eigVector ,id);

    
    
   return b;
  }

 
  static double[] diag(double[][] m) {

    double[] d = new double[m.length];
    for (int i = 0; i< m.length; i++)
      d[i] = m[i][i];
    return d;
  }

  static void divide(double[] v, double b) {

    for (int i = 0; i< v.length; i++)
      v[i] = v[i] / b;


  }
  
  static double sum(double[] a) {

    double b = a[0];
    for (int i = 0; i < a.length; i++)
      b += a[i];

    return b;

  }

 
  static double max(double[] a) {
    double b = a[0];
    for (int i = 0; i < a.length; i++)
      if (a[i] > b) b = a[i];

    return b;
  }
  
    static void doubleQuickSort(double a[], int index[], int lo0, int hi0) {
        int lo = lo0;
        int hi = hi0;
        double mid;

        if ( hi0 > lo0) {

          
            mid = a[ ( lo0 + hi0 ) / 2 ];
      
            while( lo <= hi ) {
            
                while( ( lo < hi0 ) && ( a[lo] < mid )) {
                    ++lo;
                }

              
                while( ( hi > lo0 ) && ( a[hi] > mid )) {
                    --hi;
                }

           
                if( lo <= hi ) {
                    swap(a, index, lo, hi);
                    ++lo;
                    --hi;
                }
            }
          
            if( lo0 < hi ) {
                doubleQuickSort( a, index, lo0, hi );
            }
          
            if( lo < hi0 ) {
                doubleQuickSort( a, index,lo, hi0 );
            }
        }
    }

    static private void swap(double a[], int[] index, int i, int j) {
        double T;
        T = a[i];
        a[i] = a[j];
        a[j] = T;
        // Index
        index[i] = i;
        index[j] = j;
    }
}
