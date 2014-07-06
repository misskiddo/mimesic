<?php
 
if (isset($_POST['tag']) && $_POST['tag'] != '') {
    // get tag
    $tag = $_POST['tag'];
 
    // include db handler
    require_once 'include/DB_Functions.php';
    $db = new DB_Functions();
 
    // response Array
    $response = array("tag" => $tag, "success" => 0, "error" => 0);
 
    // check for tag type
    if ($tag == 'register') {
        // Request type is Register new user
        $telefono = $_POST['telefono'];
     
        $user = $db->storeUser($telefono);
        if ($user) {
            // user stored successfully
            $response["success"] = 1;
            $response["user"]["telefono"] = $user["telefono"];
          
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = 1;
            $response["error_msg"] = "Error occured in Registartion";
            echo json_encode($response);
        }
       
    } 
       else if ($tag == 'enviar'){
       
        $dest = $_POST['destinatario'];
         $texto = $_POST['texto'];
          $result = mysql_query("INSERT INTO MIMESIC_Mensajes(destinatario, texto) VALUES ('$dest', '$texto')") ;
       if ($result) {
            $response["success"] = 1;
            echo json_encode($response);
        } else {
             $response["error"] = 1;
             echo json_encode($response);
        }
         
    }
 
     else if($tag == 'leerTodos'){
        $yo = $_POST['emisor'];
        $id = $_POST['id'];
      
         $result = mysql_query("SELECT idmensajes, texto FROM MIMESIC_Mensajes WHERE destinatario = '$yo' and idmensajes > $id order by idmensajes ") or die(mysql_error());   
          
         $posts = array();
        if(mysql_num_rows($result)){
       
          while($post = mysql_fetch_assoc($result)) {
             $posts[] = array($post);
          }
        } 
       // mysql_query("drop view aa$emisor");
         header('Content-type: application/json');
        echo json_encode($posts);
          }     
    
    else if($tag == 'pertenece'){
         $telefono = $_POST['telefono']; 
         $result = mysql_query("SELECT telefono from MIMESIC_Usuarios WHERE telefono = '$telefono'");
         $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            $response["success"] = 1;
            echo json_encode($response);
        } else {
             $response["error"] = 1;
             echo json_encode($response);
        }
    }
   
        
    else {
        echo "Invalid Request";
    }
  } 
  else {
    echo "Access Denied";
}
?>