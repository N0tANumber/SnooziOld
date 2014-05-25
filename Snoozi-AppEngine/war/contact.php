<?php
header('Content-Type: text/html; charset=utf-8');

if(isset($_POST['email'])) {
 
     
    // EDIT THE 2 LINES BELOW AS REQUIRED
 
    $email_to = "you@yourdomain.com";
 
    $email_subject = "Your email subject line";
 
   
    $first_name = $_POST['first_name']; // required 
    $email_from = $_POST['email']; // required
	$phone = $_POST['phone']; // required
    $comments = $_POST['message']; // required
 
    $email_message = "Form details below.\n\n";
 
    
    function clean_string($string) {
      $bad = array("content-type","bcc:","to:","cc:","href");
      return str_replace($bad,"",$string);
    }
 
 
    $email_message .= "Name: ".clean_string($first_name)."\n";
    $email_message .= "Email Address: ".clean_string($email_from)."\n";
	$email_message .= "Phone Number: ".clean_string($phone)."\n";
    $email_message .= "Message: ".clean_string($comments)."\n";
 
     
// create email headers
 
$headers = 'From: '.$email_from."\r\n".
 
'Reply-To: '.$email_from."\r\n" .
 
'X-Mailer: PHP/' . phpversion();
 
@mail($email_to, $email_subject, $email_message, $headers); 
 
?>
 
<!-- include your own success html here -->
 
Thank you for contacting us. We will be in touch with you very soon. 

(\n)

<a href="index.html">Return To The Site</a>
 
<?php
 
}

?>