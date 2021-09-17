import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class matrices {
    static String ip;
    static int nodo;
    static int N;
    static long checksum;
    static int[][] A;
    static int[][] B;
    static int[][] C;

    static int[][] A1;
    static int[][] B1;
    static int[][] A2;
    static int[][] B2;

    static int[][] C1;
    static int[][] C2;
    static int[][] C3;
    static int[][] C4;

    static void juntaMatriz(){
        //Juntamos C1,C2,C3,C4 en C
        for(int i=0; i<N/2 ; i++)
            for(int j=0;j<N/2;j++){
                C[i][j]=C1[i][j];
                C[i][j+N/2]=C2[i][j];
                C[i+N/2][j]=C3[i][j];
                C[i+N/2][j+N/2]=C4[i][j];
            }

    }
    static int[][] multiplicaMatriz(int[][]M1,int[][]M2,int X1X2,int Y1,int Y2){
        int [][]aux= new int[Y1][Y2];
       
        for (int i = 0; i < Y1; i++)
            for (int j = 0; j < Y2; j++)
                for (int k = 0; k < X1X2; k++){
                    aux[i][j] += M1[i][k] * M2[j][k];
                }
                   
        return aux;
    }
    static void read(DataInputStream f,byte[] b, int posicion,int longitud) throws Exception{
        while(longitud > 0){
            int n = f.read(b,posicion,longitud);
            posicion += n;
            longitud -= n;
        }
    }
    static void muestraMatriz(int[][] M,int X,int Y){
        String aux= "";
        for(int i=0; i<Y; i++){
            for(int j=0 ; j<X; j++){
                aux += M[i][j]+" ";
            }
            System.out.println(aux);
            aux="";
        }

    }
    static ByteBuffer matrizToByteBuffer(int[][] M,int X,int Y){
        ByteBuffer aux= ByteBuffer.allocate(X*Y*4);
        for(int i=0; i<Y ; i++)
            for(int j=0; j<X ; j++)
                aux.putInt(M[i][j]); 
        return aux;

    }
    static int[][] bytesToMatriz(byte []a,int X,int Y){
        int [][] M = new int[Y][X];
        ByteBuffer aux= ByteBuffer.wrap(a);
        for(int i=0 ; i<Y; i++)
            for(int j=0; j<X; j++)
                M[i][j]=aux.getInt();
        return M;
    }
    static class Worker extends Thread{
        Socket conexion;
        Worker(Socket conexion){
            this.conexion = conexion;
        }
        public void run(){   
            try{
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                int nodo_e=0;
                nodo_e=entrada.readInt();
                byte[]a = new byte[N/2*N/2*4]; 
                if(nodo_e == 1){
                    //Escribimos A1
                    salida.write(matrizToByteBuffer(A1, N, N/2).array());
                    //Escribimos B1
                    salida.write(matrizToByteBuffer(B1, N, N/2).array());
                    //Recibimos C1
                    read(entrada, a, 0, N/2*N/2*4);
                    C1=bytesToMatriz(a, N/2, N/2);
                }
                else if(nodo_e == 2){
                    //Escribimos A1
                    salida.write(matrizToByteBuffer(A1, N, N/2).array());
                    //Escribimos B2
                    salida.write(matrizToByteBuffer(B2, N, N/2).array());
                    //Recibimos C2
                    read(entrada, a, 0, N/2*N/2*4);
                    C2=bytesToMatriz(a, N/2, N/2);
                }
                else if(nodo_e == 3){
                    //Escribimos A2
                    salida.write(matrizToByteBuffer(A2, N, N/2).array());
                    //Escribimos B1
                    salida.write(matrizToByteBuffer(B1, N, N/2).array());
                    //Recibimos C3
                    read(entrada, a, 0, N/2*N/2*4);
                    C3=bytesToMatriz(a, N/2, N/2);
                }
                else{
                    //Escribimos A2
                    salida.write(matrizToByteBuffer(A2, N, N/2).array());
                    //Escribimos B2
                    salida.write(matrizToByteBuffer(B2, N, N/2).array());
                    //Recibimos C4
                    read(entrada, a, 0, N/2*N/2*4);
                    C4=bytesToMatriz(a, N/2, N/2);
                }
                salida.close();
                entrada.close();
                conexion.close();
        }
        catch(Exception e){
            System.out.println("Error:"+e.getMessage());
        }
        }
    } 

    public static void main(String[] args) throws Exception{
        if(args.length != 3){
            System.err.println("Uso:");
            System.err.println("java matrices <nodo> <ip> <N>");
            System.exit(0);
        }
        nodo = Integer.valueOf(args[0]);
        ip = args[1];
        N = Integer.valueOf(args[2]);
        if(N%2 != 0){
            System.err.println("N debe ser par");
            System.exit(0);
        }
        if(nodo == 0){
            //Nodo 0
            A = new int[N][N];
            B = new int[N][N];
            C = new int[N][N];
            A1 = new int[N/2][N];
            A2 = new int[N/2][N];
            B1 = new int[N/2][N];
            B2 = new int[N/2][N];

            //Inicializamos las matrices
            for(int i=0; i<N; i++)
               for(int j=0 ; j<N; j++){
                   A[i][j] = i+3*j;
                   B[i][j] = i-3*j;
               }
            if(N<=10){
                //Mostramos matriz A y B
                System.out.println("Matriz A");
                muestraMatriz(A, N,N);
                System.out.println("Matriz B");
                muestraMatriz(B, N,N);
            }           
            //Trasponemos la matriz B
           for (int i = 0; i < N; i++)
                for (int j = 0; j < i; j++){
                    int x = B[i][j];
                    B[i][j] = B[j][i];
                    B[j][i] = x;
            }
            //Llenasmos la matriz A1 y A2
            for (int i=0 ; i<N ;i++){
                if(i<N/2){
                    for(int j=0 ;j<N;j++){
                        A1[i][j]=A[i][j];
                    }
                }
                else{
                    for(int j=0 ;j<N;j++){
                        A2[i-N/2][j]=A[i][j];
                    }
                }
            }
            //LLenamos la matriz B1 y B2
            for (int i=0 ; i<N ;i++){
                if(i<N/2){
                    for(int j=0 ;j<N;j++){
                        B1[i][j]=B[i][j];
                    }
                }
                else{
                    for(int j=0 ;j<N;j++){
                        B2[i-N/2][j]=B[i][j];
                    }
                }
            }
            ServerSocket servidor = new ServerSocket(40000);
            Worker[] v= new Worker[4]; 
            for(int i = 0 ; i < 4; i++ ){
                Socket conexion = servidor.accept();
                Worker w = new Worker(conexion);
                v[i]=w;
                v[i].start();
            } 
            for(int i = 0 ; i < 4; i++ ){
                v[i].join();
            }
            servidor.close();
            juntaMatriz();
            //Calculamos el checksum
            for(int i=0; i<N ; i++)
                for(int j=0 ; j<N ; j++)
                    checksum+=C[i][j];
            if(N<=10){
                //Mostramos la matriz resultante
                System.out.println("Matriz C");
                muestraMatriz(C, N,N);
            }
            System.out.println("Checksum="+checksum);
        }
        else{
            //Demas nodos
            Socket conexion=null;
            for(;;) 
                try{
                  conexion = new Socket(ip,40000); 
                  break;
                }
                catch(Exception e){
                    Thread.sleep(100);
                }
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            int [][]M1;
            int [][]M2;
            int [][]aux;
            salida.writeInt(nodo);
            byte[]a = new byte[N*N/2*4];
            read(entrada, a, 0, N*N/2*4);
            M1=bytesToMatriz(a, N, N/2);
            read(entrada, a, 0, N*N/2*4);
            M2=bytesToMatriz(a, N, N/2);
            aux=multiplicaMatriz(M1, M2, N, N/2, N/2);
            salida.write(matrizToByteBuffer(aux, N/2, N/2).array());
            entrada.close();
            salida.close();
            conexion.close();
        }
    }


}
