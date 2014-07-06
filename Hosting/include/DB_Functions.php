<?php
 
class DB_Functions {
 
    private $db;
 
    //put your code here
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    // destructor
    function __destruct() {
 
    }
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($telefono) {
       
        $result = mysql_query("INSERT INTO MIMESIC_Usuarios(telefono, created_at) VALUES('$telefono', NOW())");
        // check for successful store
        if ($result) {
            // get user details
            $uid = mysql_insert_id(); // last inserted id
            $result = mysql_query("SELECT * FROM MIMESIC_Usuarios WHERE uid = $uid");
            // return user details
            return mysql_fetch_array($result);
        } else {                                        
            return false;
        }
    }
    
    public function modificarPass($telefono, $password) {
       
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
        $result = mysql_query("update MIMESIC_Usuarios set encrypted_password='$encrypted_password', salt = '$salt' where telefono = '$telefono'");
        // check for successful store
        if ($result) {
           
            return true;
        } else {
            return false;
        }
    }
  
            
 
    /**
     * Get user by email and password
     */
    public function getUserByTlfAndPassword($telefono, $password) {
        $result = mysql_query("SELECT * FROM MIMESIC_Usuarios WHERE telefono = '$telefono'") or die(mysql_error());
        // check for result
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
            $salt = $result['salt'];
            $encrypted_password = $result['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $result;
            }
        } else {
            // user not found
            return false;
        }
    }
 
    /**
     * Check user is existed or not
     */
    public function isUserExisted($telefono) {
        $result = mysql_query("SELECT telefono from MIMESIC_Usuarios WHERE telefono = '$telefono'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed
            return true;
        } else {
            // user not existed
            return false;
        }
    }
 
     
}
 
?>