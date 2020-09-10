import java.util.Scanner;

class Program
{
    public static void main(String[] args)
    {
        Scanner s = new Scanner(System.in);
        String ln = s.nextLine();
        int n = Integer.parseInt(ln);
        
        for(int i = 0; i < n; i++)
        {
            String[] stl = s.nextLine().split(" ");
            int a = Integer.parseInt(stl[0]);
            int b = Integer.parseInt(stl[1]);
            
            int[] h = new int[a];
            String[] asdadasdasd = s.nextLine().split(" ");
            for(int j = 0; j < a; j++)
            {
                h[j] = Integer.parseInt(asdadasdasd[j]);
            }
            
            boolean f = true;
            int itrs = 0;
            while(f && itrs + 1 != a)
            {
                f = false;
                int smolest = Integer.MAX_VALUE;
                int smI = -1;
                for(int k = itrs; k < a; k++)
                {
                    if(h[k] < smolest)
                    {
                        smolest = h[k];
                        smI = k;
                    }
                }
                
                if(smolest <= b)
                {
                	b -= smolest;
                    f = true;
                    int temp = h[smI];
                    h[smI] = h[itrs];
                    h[itrs] = temp;
                    itrs++;
                }
            }
            
            System.out.println("Case #" + (i + 1) + ": " + itrs);
        }
    }
}