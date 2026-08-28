import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    User user;
    JTabbedPane tabs = new JTabbedPane();

    public AdminDashboard(User user) {
        this.user = user;
        setTitle("HealthFirst - Admin Dashboard"); setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setLocationRelativeTo(null);
        add(tabs); add(header(), BorderLayout.NORTH);
        tabs.add("Medicines", medicinesPanel());
        tabs.add("Suppliers", suppliersPanel());
        tabs.add("Users", usersPanel());
        tabs.add("Reports", reportsPanel());
    }

    JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("  Admin: " + user.fullName), BorderLayout.WEST);
        JButton logout = new JButton("Logout"); p.add(logout, BorderLayout.EAST);
        logout.addActionListener(e -> { dispose(); new Login().setVisible(true); });
        return p;
    }

    JPanel medicinesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        String[] cols = {"ID","Name","Company","Type","Price","Stock","Reorder","Expiry","Supplier ID"};
        DefaultTableModel m = new DefaultTableModel(cols,0);
        JTable t = new JTable(m); loadMedicines(m); p.add(new JScrollPane(t),BorderLayout.CENTER);
        JPanel form = new JPanel(new GridLayout(2,10));
        JTextField[] f = new JTextField[8]; for(int i=0;i<8;i++) f[i]=new JTextField();
        String[] labels={"Name","Company","Type","Price","Stock","Reorder","Expiry (YYYY-MM-DD)","Supplier ID"};
        for(int i=0;i<8;i++){form.add(new JLabel(labels[i])); form.add(f[i]);}
        JButton add=new JButton("Add"), update=new JButton("Update"), delete=new JButton("Delete"), refresh=new JButton("Refresh");
        JPanel b=new JPanel(); b.add(add);b.add(update);b.add(delete);b.add(refresh);
        p.add(form,BorderLayout.SOUTH); p.add(b,BorderLayout.NORTH);
        add.addActionListener(e->medicineAction(f,m,null));
        update.addActionListener(e->{int r=t.getSelectedRow(); if(r>=0) medicineAction(f,m,Integer.parseInt(m.getValueAt(r,0).toString()));});
        delete.addActionListener(e->{int r=t.getSelectedRow(); if(r>=0) delete("medicines","medicine_id",Integer.parseInt(m.getValueAt(r,0).toString())); loadMedicines(m);});
        refresh.addActionListener(e->loadMedicines(m));
        t.getSelectionModel().addListSelectionListener(e->{int r=t.getSelectedRow(); if(r>=0) for(int i=0;i<8;i++) f[i].setText(m.getValueAt(r,i+1).toString());});
        return p;
    }

    void medicineAction(JTextField[] f, DefaultTableModel m, Integer id){
        try(Connection c=DB.getConnection()){
            String sql=id==null?"INSERT INTO medicines(name,company,medicine_type,price,quantity_in_stock,reorder_level,expiry_date,supplier_id) VALUES(?,?,?,?,?,?,?,?)":"UPDATE medicines SET name=?,company=?,medicine_type=?,price=?,quantity_in_stock=?,reorder_level=?,expiry_date=?,supplier_id=? WHERE medicine_id=?";
            PreparedStatement ps=c.prepareStatement(sql); for(int i=0;i<8;i++) ps.setString(i+1,f[i].getText()); if(id!=null) ps.setInt(9,id); ps.executeUpdate(); loadMedicines(m); JOptionPane.showMessageDialog(this,"Saved successfully.");
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }
    void loadMedicines(DefaultTableModel m){m.setRowCount(0); try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT * FROM medicines")){while(r.next())m.addRow(new Object[]{r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getDouble(5),r.getInt(6),r.getInt(7),r.getDate(8),r.getInt(9)});}catch(Exception e){}}

    JPanel suppliersPanel(){
        JPanel p=new JPanel(new BorderLayout()); String[] cols={"ID","Name","Contact","Phone","Email","Address"}; DefaultTableModel m=new DefaultTableModel(cols,0); JTable t=new JTable(m); loadSuppliers(m); p.add(new JScrollPane(t));
        JPanel f=new JPanel(new GridLayout(2,6)); JTextField[] x=new JTextField[5]; for(int i=0;i<5;i++)x[i]=new JTextField(); String[] l={"Name","Contact","Phone","Email","Address"}; for(int i=0;i<5;i++){f.add(new JLabel(l[i]));f.add(x[i]);} JButton add=new JButton("Add"),del=new JButton("Delete"),ref=new JButton("Refresh"); JPanel b=new JPanel();b.add(add);b.add(del);b.add(ref);p.add(f,BorderLayout.SOUTH);p.add(b,BorderLayout.NORTH);
        add.addActionListener(e->{try(Connection c=DB.getConnection();PreparedStatement q=c.prepareStatement("INSERT INTO suppliers(name,contact_person,phone,email,address) VALUES(?,?,?,?,?)")){for(int i=0;i<5;i++)q.setString(i+1,x[i].getText());q.executeUpdate();loadSuppliers(m);}catch(Exception z){JOptionPane.showMessageDialog(this,z.getMessage());}});
        del.addActionListener(e->{int r=t.getSelectedRow();if(r>=0){delete("suppliers","supplier_id",Integer.parseInt(m.getValueAt(r,0).toString()));loadSuppliers(m);}});ref.addActionListener(e->loadSuppliers(m)); return p;
    }
    void loadSuppliers(DefaultTableModel m){m.setRowCount(0);try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT * FROM suppliers")){while(r.next())m.addRow(new Object[]{r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getString(6)});}catch(Exception e){}}

    JPanel usersPanel(){
        JPanel p=new JPanel(new BorderLayout());String[] cols={"ID","Username","Role","Full Name"};DefaultTableModel m=new DefaultTableModel(cols,0);JTable t=new JTable(m);loadUsers(m);p.add(new JScrollPane(t));
        JPanel f=new JPanel(new GridLayout(2,6));JTextField u=new JTextField(),pw=new JTextField(),name=new JTextField();JComboBox<String> role=new JComboBox<>(new String[]{"Cashier","Admin"});f.add(new JLabel("Username"));f.add(u);f.add(new JLabel("Password"));f.add(pw);f.add(new JLabel("Role"));f.add(role);f.add(new JLabel("Full Name"));f.add(name);JButton add=new JButton("Create User"),del=new JButton("Delete User");f.add(add);f.add(del);p.add(f,BorderLayout.SOUTH);
        add.addActionListener(e->{try(Connection c=DB.getConnection();PreparedStatement q=c.prepareStatement("INSERT INTO users(username,password,role,full_name) VALUES(?,?,?,?)")){q.setString(1,u.getText());q.setString(2,pw.getText());q.setString(3,role.getSelectedItem().toString());q.setString(4,name.getText());q.executeUpdate();loadUsers(m);}catch(Exception z){JOptionPane.showMessageDialog(this,z.getMessage());}});
        del.addActionListener(e->{int r=t.getSelectedRow();if(r>=0){delete("users","user_id",Integer.parseInt(m.getValueAt(r,0).toString()));loadUsers(m);}});return p;
    }
    void loadUsers(DefaultTableModel m){m.setRowCount(0);try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT user_id,username,role,full_name FROM users")){while(r.next())m.addRow(new Object[]{r.getInt(1),r.getString(2),r.getString(3),r.getString(4)});}catch(Exception e){}}

    JPanel reportsPanel(){JPanel p=new JPanel(new GridLayout(2,2,10,10));String[] names={"Sales Report","Item-Wise Report","Low Stock Report","Expiry Report"};String[] sql={"SELECT DATE(sale_date) Date, COUNT(*) Sales, SUM(total_amount) Total FROM sales GROUP BY DATE(sale_date) ORDER BY Date DESC","SELECT m.name, SUM(si.quantity_sold) Quantity, SUM(si.quantity_sold*si.price_at_sale) Revenue FROM sale_items si JOIN medicines m ON si.medicine_id=m.medicine_id GROUP BY m.medicine_id,m.name ORDER BY Quantity DESC","SELECT name,quantity_in_stock,reorder_level FROM medicines WHERE quantity_in_stock<=reorder_level ORDER BY quantity_in_stock","SELECT name,expiry_date,quantity_in_stock FROM medicines WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(),INTERVAL 1 MONTH) ORDER BY expiry_date"};for(int i=0;i<4;i++){JButton b=new JButton(names[i]);final int k=i;b.addActionListener(e->report(sql[k],names[k]));p.add(b);}return p;}
    void report(String sql,String title){JTextArea a=new JTextArea();try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){ResultSetMetaData md=r.getMetaData();while(r.next()){for(int i=1;i<=md.getColumnCount();i++)a.append(md.getColumnName(i)+": "+r.getString(i)+"  ");a.append("\n");}}catch(Exception e){a.setText(e.getMessage());}JOptionPane.showMessageDialog(this,new JScrollPane(a),title,JOptionPane.INFORMATION_MESSAGE);}
    void delete(String table,String key,int id){try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM "+table+" WHERE "+key+"=?")){p.setInt(1,id);p.executeUpdate();}catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}}
}
