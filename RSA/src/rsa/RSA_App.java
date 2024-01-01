package rsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.lang.Math.sqrt;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;


public class RSA_App extends javax.swing.JFrame {
    //Khai báo biến:
    private long soP, soQ, soD,soN, soE, phiN;
    private boolean isConfirm = false;
    private File file;
    
    public RSA_App() {
        initComponents();
        this.setLocationRelativeTo(null);
    }
    
    //Các hàm phụ trợ:
    
    //Hàm kiểm tra số nguyên tố:
    private boolean KtraSoNguyenTo(long p)
    {
        if(p<2){
            return false;
        }
        long dem=0;
        for(long i=2; i <= sqrt(p); i++)
        {
            if(p%i==0){
                dem++;
            }
        }
        
        return dem==0 ? true : false;
    }
    
    //Hàm bình phương nhân:
    private long BPN(long mx, long ex, long nx)
    {
        //Chuyển e sang hệ nhị phân
        long[] a = new long[100];
        int k = 0;
        do
        {
            a[k] = ex % 2;
            k++;
            ex = ex / 2;
        }
        while (ex != 0);
        //Quá trình lấy dư
        long kq = 1;
        for (int i = k - 1; i >= 0; i--)
        {
            kq = (kq * kq) % nx;
            if (a[i] == 1)
                kq = (kq * mx) % nx;
        }
        return kq;
    }
    
    //Hàm ước chung lớn nhất:
    private long GCD(long a, long b)
    {
        if(a > b){
            long tg = a;
            a = b;
            b = tg;
        }
        if(a==0) return b;
        if(b%a==0) return a;
        else return GCD(b%a,a);
    }
    
    //Hàm tìm số nghịch đảo:
    private long NghichDao(long x, long m) {
        long r2 = 0, r1 = x, r0 = m;
        long q = 0, q1 = 0, q0 = 0;
        long t = 0, t0 = 0, t1 = 1,count = 0;

        while (true)
        {
                count++;
                q0 = q1;
                q1 = q;
                q = r0 / r1;		

                r2 = r0 % r1;
                r0 = r1;
                r1 = r2;

                if (count > 2){
                        t = t0 - q0 * t1;
                        t0 = t1;
                        t1 = t;
                }
                if (r2 == 0){
                    t = t0 - q1 * t1;
                    if (t < 0) t = t + m;
                    else if (t >= m)t = t - m;
                    return t;
                }
        }
    }
    
    //Hàm tạo khóa:
    private void CreateKey(){
        Random rd = new Random();
        //Set số P random từ 80 - 150 + 80
        soP = rd.nextLong(150) + 80;

        //Kiểm tra xem số P có phải số nguyên tố không! nếu không phải thì set lại
        while(!KtraSoNguyenTo(soP)){
            soP = rd.nextLong(150) + 80;
        }
        
        //Set số Q random từ 80 - 150 + 80
        soQ = rd.nextLong(150) + 80;
        //Kiểm tra xem số Q có phải số nguyên tố không! nếu không phải thì set lại
        while(!KtraSoNguyenTo(soQ) || soQ == soP){
            soQ = rd.nextLong(150) + 80;
        }
        
        
        phiN = (soP-1)*(soQ-1);
        soN = soP * soQ;
        
        //Set số D random từ 80 - 150
        soD = rd.nextLong(150) + 80;
        //Kiểm tra xem số D có phải số nguyên tố cùng nhau với phiN! nếu không phải thì set lại
        while(GCD(soD, phiN) != 1){
            soD = rd.nextLong(150) + 80;
        }
        
        soE = NghichDao(soD,phiN);
    }
    
    //Set text cho EdtText
    private void setETValue(){
        Edt_p.setText(String.valueOf(soP));
        Edt_q.setText(String.valueOf(soQ));
        Edt_d.setText(String.valueOf(soD));
        Edt_n.setText(String.valueOf(soN));
        Edt_phiN.setText(String.valueOf(phiN));
        Edt_e.setText(String.valueOf(soE));
    }
    
    //Kiểm tra khóa và show dialog
    private void checkKey(){
        //Ktra xem các ô còn trống không
        if( Edt_q.getText().trim().equals("") ||
            Edt_p.getText().trim().equals("") ||
            Edt_d.getText().trim().equals(""))
            JOptionPane.showMessageDialog(rootPane, "Nhập đủ các ô!", "ERROR", HEIGHT);
        
        //Gắn giá trị cho các số:
        soQ = Long.parseLong(Edt_q.getText());        
        soP = Long.parseLong(Edt_p.getText());
        soD = Long.parseLong(Edt_d.getText());
        
        //Kiểm tra xem p có phải số nguyên tố không
        if(!KtraSoNguyenTo(soP)) 
            JOptionPane.showMessageDialog(rootPane, "Số p không phải là số nguyên tố!", "ERROR", HEIGHT);
        
        //Kiểm tra xem q có phải số nguyên tố không
        else if(!KtraSoNguyenTo(soQ)) 
            JOptionPane.showMessageDialog(rootPane, "Số q không phải là số nguyên tố!", "ERROR", HEIGHT);
        else if(soQ == soP) 
            JOptionPane.showMessageDialog(rootPane, "p và q bị trùng nhau!", "ERROR", HEIGHT);
        else if(GCD(soD, phiN) != 1)
            JOptionPane.showMessageDialog(rootPane, "Ước chung lớn nhất của d và øn không bằng 1", "ERROR", HEIGHT);
        
        else{
            setETValue();
            JOptionPane.showConfirmDialog(rootPane, "Tạo khóa thành công!", "Success", HEIGHT);
            isConfirm = true;

        }
        
        phiN = (soP-1)*(soQ-1);
        soN = soP * soQ;
        soE = NghichDao(soD,phiN);
        
    }
    
    //hàm giải mã
    public void GiaiMa(String ChuoiVao)
    {
        //decode: chuyển đổi dữ liệu từ ascii sang nhị phân
        byte[] gm_temp1 = Base64.getDecoder().decode(ChuoiVao);
        String giaima = new String(gm_temp1, StandardCharsets.UTF_8);
        
        // Chuyen xau thanh ma Unicode
        long[] gm_temp2 = new long[giaima.length()];
        for (int i = 0; i < giaima.length(); i++)
        {
            //b[i] = (long)giaima[i];
            gm_temp2[i] = giaima.charAt(i);
        }
        
        //Giải mã
        long[] gm_temp3 = new long[gm_temp2.length];
        for (int i = 0; i < gm_temp3.length; i++)
        {
            gm_temp3[i] = BPN(gm_temp2[i], soD, soN);// giải mã
        }
        
        //Chuyển sang kiểu kí tự trong bảng mã Unicode
        String str = "";
        for (int i = 0; i < gm_temp3.length; i++)
        {
            str = str + (char)gm_temp3[i];
        }
        byte[] data2 = Base64.getDecoder().decode(str);
        String gm = new String(data2, StandardCharsets.UTF_8);
        BR_Nhan.setText(gm);
    }
    //hàm mã hóa
    public void MaHoa(String ChuoiVao)
    {
        // Chuyen xau thanh ma Unicode
        byte[] mh_temp1 = ChuoiVao.getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.getEncoder().encodeToString(mh_temp1);

        
        long[] mh_temp2 = new long[base64.length()];
        for (int i = 0; i < base64.length(); i++)
        {
            mh_temp2[i] = base64.charAt(i);
        }
        
        //Mã hóa
        long[] mh_temp3 = new long[mh_temp2.length];
        for (int i = 0; i < mh_temp2.length; i++)
        {
            mh_temp3[i] = BPN(mh_temp2[i], soE, soN); // mã hóa
        }
        
        //Chuyển sang kiểu kí tự trong bảng mã Unicode
        String str = "";
        for (int i = 0; i < mh_temp3.length; i++)
        {
            str = str + (char)mh_temp3[i];
        }
        byte[] data = str.getBytes(StandardCharsets.UTF_8);
        BM_Gui.setText(Base64.getEncoder().encodeToString(data)); 
    }
    //reset
    private void reset(){
        isConfirm = false;
        Edt_p.setText("");
        Edt_q.setText("");
        Edt_d.setText("");
        Edt_n.setText("");
        Edt_phiN.setText("");
        Edt_e.setText("");
        BR_Gui.setText("");
        BR_Nhan.setText("");
        BM_Gui.setText("");
        BM_Nhan.setText("");
    }
    
    
    //Lưu file
    private void save(String text) throws IOException {
        FileOutputStream fos = new  FileOutputStream(file.getAbsolutePath());
        OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
        PrintWriter pw = new PrintWriter(osw, true);
        pw.print(text);
        pw.close();
    }
    private void chooseFile( JTextArea edt){
        JFileChooser ChoseFile = new JFileChooser();
        FileNameExtensionFilter TextFile = new FileNameExtensionFilter("Chọn file txt","txt","TXT");
        ChoseFile.setFileFilter(TextFile);
        ChoseFile.setMultiSelectionEnabled(false);

        long x = ChoseFile.showDialog(this,"Chọn File");
        if( x == ChoseFile.APPROVE_OPTION )
        {
            file = ChoseFile.getSelectedFile();
        }   
        try {
            save(edt.getText());
            JOptionPane.showMessageDialog(rootPane,"Save done!");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    //Đọc file
    private String Read() throws IOException {
        String value = "";
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        while((line = br.readLine()) != null){
            value += line.trim();
        }
        return value;
    }
    private String Read1() throws IOException {
        String value = "";
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        while((line = br.readLine()) != null){
            value += line.trim() + "\n";
        }
        return value;
    }
    private void readFile(JTextArea edt){
        JFileChooser ChoseFile = new JFileChooser();
        FileNameExtensionFilter TextFile = new FileNameExtensionFilter("Chọn file txt","txt","TXT");
        ChoseFile.setFileFilter(TextFile);
        ChoseFile.setMultiSelectionEnabled(false);

        long x = ChoseFile.showDialog(this,"Chọn File");
        if( x == ChoseFile.APPROVE_OPTION )
        {
            file = ChoseFile.getSelectedFile();
            try {
                edt.setText(Read());
            } catch (IOException ex) {
                 throw new RuntimeException(ex);
            }
        }
    }
    private void readFile1(JTextArea edt){
        JFileChooser ChoseFile = new JFileChooser();
        FileNameExtensionFilter TextFile = new FileNameExtensionFilter("Chọn file txt","txt","TXT");
        ChoseFile.setFileFilter(TextFile);
        ChoseFile.setMultiSelectionEnabled(false);

        long x = ChoseFile.showDialog(this,"Chọn File");
        if( x == ChoseFile.APPROVE_OPTION )
        {
            file = ChoseFile.getSelectedFile();
            try {
                edt.setText(Read1());
            } catch (IOException ex) {
                 throw new RuntimeException(ex);
            }
        }
    }
    //layout
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        BR_Gui = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        BM_Gui = new javax.swing.JTextArea();
        Btn_FileGui = new javax.swing.JButton();
        Btn_LuuGui = new javax.swing.JButton();
        Btn_MaHoa = new javax.swing.JButton();
        Btn_Gui = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        BR_Nhan = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        BM_Nhan = new javax.swing.JTextArea();
        Btn_FileNhan = new javax.swing.JButton();
        Btn_LuuNhan = new javax.swing.JButton();
        Btn_GiaiMa = new javax.swing.JButton();
        Btn_Reset = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        Edt_p = new javax.swing.JTextField();
        Edt_q = new javax.swing.JTextField();
        Edt_d = new javax.swing.JTextField();
        Edt_e = new javax.swing.JTextField();
        Edt_n = new javax.swing.JTextField();
        Edt_phiN = new javax.swing.JTextField();
        Btn_random = new javax.swing.JButton();
        Btn_TaoKhoa = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Bản rõ:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Bản mã:");

        BR_Gui.setColumns(20);
        BR_Gui.setRows(5);
        jScrollPane1.setViewportView(BR_Gui);

        BM_Gui.setColumns(20);
        BM_Gui.setRows(5);
        jScrollPane2.setViewportView(BM_Gui);

        Btn_FileGui.setBackground(new java.awt.Color(102, 102, 102));
        Btn_FileGui.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Btn_FileGui.setForeground(new java.awt.Color(51, 51, 51));
        Btn_FileGui.setText("File");
        Btn_FileGui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_FileGuiActionPerformed(evt);
            }
        });

        Btn_LuuGui.setBackground(new java.awt.Color(102, 102, 102));
        Btn_LuuGui.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Btn_LuuGui.setForeground(new java.awt.Color(51, 51, 51));
        Btn_LuuGui.setText("Lưu");
        Btn_LuuGui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_LuuGuiActionPerformed(evt);
            }
        });

        Btn_MaHoa.setBackground(new java.awt.Color(102, 102, 102));
        Btn_MaHoa.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        Btn_MaHoa.setForeground(new java.awt.Color(51, 51, 51));
        Btn_MaHoa.setText("Mã hóa");
        Btn_MaHoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_MaHoaActionPerformed(evt);
            }
        });

        Btn_Gui.setBackground(new java.awt.Color(102, 102, 102));
        Btn_Gui.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        Btn_Gui.setForeground(new java.awt.Color(51, 51, 51));
        Btn_Gui.setText("Gửi");
        Btn_Gui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_GuiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Btn_LuuGui, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Btn_FileGui, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 15, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(Btn_MaHoa))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(162, 162, 162)
                        .addComponent(Btn_Gui)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addComponent(Btn_FileGui)))
                .addGap(18, 18, 18)
                .addComponent(Btn_MaHoa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Btn_Gui)
                        .addGap(19, 19, 19))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(154, 154, 154))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(Btn_LuuGui)
                        .addGap(146, 146, 146))))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setPreferredSize(new java.awt.Dimension(416, 519));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Bản mã:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Bản rõ:");

        BR_Nhan.setColumns(20);
        BR_Nhan.setRows(5);
        jScrollPane4.setViewportView(BR_Nhan);

        BM_Nhan.setColumns(20);
        BM_Nhan.setRows(5);
        jScrollPane3.setViewportView(BM_Nhan);

        Btn_FileNhan.setBackground(new java.awt.Color(102, 102, 102));
        Btn_FileNhan.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Btn_FileNhan.setForeground(new java.awt.Color(51, 51, 51));
        Btn_FileNhan.setText("File");
        Btn_FileNhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_FileNhanActionPerformed(evt);
            }
        });

        Btn_LuuNhan.setBackground(new java.awt.Color(102, 102, 102));
        Btn_LuuNhan.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Btn_LuuNhan.setForeground(new java.awt.Color(51, 51, 51));
        Btn_LuuNhan.setText("Lưu");
        Btn_LuuNhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_LuuNhanActionPerformed(evt);
            }
        });

        Btn_GiaiMa.setBackground(new java.awt.Color(102, 102, 102));
        Btn_GiaiMa.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        Btn_GiaiMa.setForeground(new java.awt.Color(51, 51, 51));
        Btn_GiaiMa.setText("Giải mã");
        Btn_GiaiMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_GiaiMaActionPerformed(evt);
            }
        });

        Btn_Reset.setBackground(new java.awt.Color(102, 102, 102));
        Btn_Reset.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        Btn_Reset.setForeground(new java.awt.Color(51, 51, 51));
        Btn_Reset.setText("Reset");
        Btn_Reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_ResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Btn_LuuNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Btn_FileNhan, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 3, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(Btn_GiaiMa, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(154, 154, 154))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(Btn_Reset)
                        .addGap(152, 152, 152))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addGap(153, 153, 153))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(Btn_FileNhan)))
                .addGap(18, 18, 18)
                .addComponent(Btn_GiaiMa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Btn_Reset)
                        .addGap(19, 19, 19))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(Btn_LuuNhan)
                        .addGap(153, 153, 153))))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("GỬI:");

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD, jLabel2.getFont().getSize()+6));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("NHẬN:");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("p:");

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("q:");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel5.setText("d:");

        jLabel6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel6.setText("n:");

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel7.setText("Øn:");

        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel8.setText("e:");

        Edt_p.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Edt_p.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        Edt_p.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                Edt_pInputMethodTextChanged(evt);
            }
        });

        Edt_q.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        Edt_d.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        Edt_e.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        Edt_n.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        Edt_phiN.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        Btn_random.setBackground(new java.awt.Color(102, 102, 102));
        Btn_random.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Btn_random.setForeground(new java.awt.Color(51, 51, 51));
        Btn_random.setText("Random");
        Btn_random.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_randomActionPerformed(evt);
            }
        });

        Btn_TaoKhoa.setBackground(new java.awt.Color(102, 102, 102));
        Btn_TaoKhoa.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Btn_TaoKhoa.setForeground(new java.awt.Color(51, 51, 51));
        Btn_TaoKhoa.setText("Tạo khóa");
        Btn_TaoKhoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_TaoKhoaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Edt_p, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Edt_q, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(Edt_n, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(32, 32, 32)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Edt_phiN, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Edt_d, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Edt_e, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Btn_random)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(52, 52, 52)
                        .addComponent(Btn_TaoKhoa)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Btn_random)
                            .addComponent(Btn_TaoKhoa))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(Edt_p, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Edt_q, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Edt_d, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(Edt_n, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Edt_phiN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Edt_e, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    //Set sự kiện cho nút giải mã
    private void Btn_GiaiMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_GiaiMaActionPerformed
        if(!isConfirm){
            JOptionPane.showMessageDialog(rootPane, "Chưa tạo khóa!", "ERROR", HEIGHT);
        }
        else if(!BM_Nhan.getText().equals(""))
        {
            GiaiMa(BM_Nhan.getText());
        }
        else JOptionPane.showMessageDialog(rootPane, "Bản mã chưa được nhập!", "ERROR", HEIGHT);
    }//GEN-LAST:event_Btn_GiaiMaActionPerformed
    //Set sự kiện cho nút random
    private void Btn_randomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_randomActionPerformed
        isConfirm = false;
        CreateKey();
        Edt_p.setText(String.valueOf(soP));
        Edt_q.setText(String.valueOf(soQ));
        Edt_d.setText(String.valueOf(soD));
        Edt_n.setText("");
        Edt_phiN.setText("");
        Edt_e.setText("");
    }//GEN-LAST:event_Btn_randomActionPerformed
    //Set sự kiện cho nút tạo khóa
    private void Btn_TaoKhoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_TaoKhoaActionPerformed
        checkKey();
    }//GEN-LAST:event_Btn_TaoKhoaActionPerformed
    //Set sự kiện cho nút reset
    private void Btn_ResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_ResetActionPerformed
        isConfirm = false;
        reset();
    }//GEN-LAST:event_Btn_ResetActionPerformed
    //Set sự kiện cho nút lưu bên gửi
    private void Btn_LuuGuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_LuuGuiActionPerformed
        if(BM_Gui.getText().trim().equals(""))
        {
            JOptionPane.showMessageDialog(rootPane, "Bản mã trống!");
        }
        else {
            chooseFile(BM_Gui);
        }
    }//GEN-LAST:event_Btn_LuuGuiActionPerformed
    //Set sự kiện cho nút lưu bên nhận
    private void Btn_LuuNhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_LuuNhanActionPerformed
         if(BR_Nhan.getText().trim().equals(""))
        {
            JOptionPane.showMessageDialog(rootPane, "Bản rõ trống!");
        }
        else {
            chooseFile(BR_Nhan);
        }
    }//GEN-LAST:event_Btn_LuuNhanActionPerformed
    //Set sự kiện cho nút file bên gửi
    private void Btn_FileGuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_FileGuiActionPerformed
        readFile1(BR_Gui);
    }//GEN-LAST:event_Btn_FileGuiActionPerformed
    //Set sự kiện cho nút file bên nhận
    private void Btn_FileNhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_FileNhanActionPerformed
        readFile(BM_Nhan);
    }//GEN-LAST:event_Btn_FileNhanActionPerformed
    //Set sự kiện cho nút mã hóa bên nhận
    private void Btn_MaHoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_MaHoaActionPerformed
         if(!isConfirm){
            JOptionPane.showMessageDialog(rootPane, "Chưa tạo khóa!", "ERROR", HEIGHT);
        }
        else if(BR_Gui.getText().trim().equals(""))
        {
            JOptionPane.showMessageDialog(rootPane, "Bản rõ chưa được nhập!", "ERROR", HEIGHT);
            
        }
        else MaHoa(BR_Gui.getText());
    }//GEN-LAST:event_Btn_MaHoaActionPerformed

    private void Edt_pInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_Edt_pInputMethodTextChanged

    }//GEN-LAST:event_Edt_pInputMethodTextChanged
    //Set sự kiện cho nút gửi
    private void Btn_GuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_GuiActionPerformed
        if(BM_Gui.getText().equals(""))
        {
            JOptionPane.showMessageDialog(rootPane, "Bản mã trống!", "ERROR", HEIGHT);
            
        }
        else BM_Nhan.setText(BM_Gui.getText());
    }//GEN-LAST:event_Btn_GuiActionPerformed

    
    public static void main(String args[]) {
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RSA_App().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea BM_Gui;
    private javax.swing.JTextArea BM_Nhan;
    private javax.swing.JTextArea BR_Gui;
    private javax.swing.JTextArea BR_Nhan;
    private javax.swing.JButton Btn_FileGui;
    private javax.swing.JButton Btn_FileNhan;
    private javax.swing.JButton Btn_GiaiMa;
    private javax.swing.JButton Btn_Gui;
    private javax.swing.JButton Btn_LuuGui;
    private javax.swing.JButton Btn_LuuNhan;
    private javax.swing.JButton Btn_MaHoa;
    private javax.swing.JButton Btn_Reset;
    private javax.swing.JButton Btn_TaoKhoa;
    private javax.swing.JButton Btn_random;
    private javax.swing.JTextField Edt_d;
    private javax.swing.JTextField Edt_e;
    private javax.swing.JTextField Edt_n;
    private javax.swing.JTextField Edt_p;
    private javax.swing.JTextField Edt_phiN;
    private javax.swing.JTextField Edt_q;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    // End of variables declaration//GEN-END:variables
}
