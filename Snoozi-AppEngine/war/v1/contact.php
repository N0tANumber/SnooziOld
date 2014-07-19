<?php
header('Content-Type: text/html; charset=utf-8');

$myresult = "Oups, it's seems your email is wrong...";
if(isset($_GET['email'])) 
{
 
     
    // EDIT THE 2 LINES BELOW AS REQUIRED
 
    //$email_to = "contact@snoozi.co";
    $email_to = "snooziteam@gmail.com";
 
    $email_subject = "Je souhaite participer a la BETA de Snoozi";
 
   
    //$first_name = $_POST['first_name']; // required 
    $email_from = $_GET['email']; // required
	//$phone = $_POST['phone']; // required
    //$comments = $_POST['message']; // required
 
    $email_message = "Sended from the website:\n\n";
 
    
    function clean_string($string) {
      $bad = array("content-type","bcc:","to:","cc:","href");
      return str_replace($bad,"",$string);
    }
 
 
    //$email_message .= "Name: ".clean_string($first_name)."\n";
    $email_message .= "Email Address: ".clean_string($email_from)."\n";
	//$email_message .= "Phone Number: ".clean_string($phone)."\n";
    //$email_message .= "Message: ".clean_string($comments)."\n";
 
     
// create email headers
use \google\appengine\api\mail\Message;

//$image_content_id = '<image-content-id>';

try
{
  $message = new Message();
  $message->setSender($email_from);
  $message->addTo($email_to);
  $message->setSubject($email_subject);
  $message->setTextBody($email_message);
  //$message->addAttachment('image.jpg', 'image data', $image_content_id);
  
  $myresult = "Thank you for applying !<br> We will send you an email for testing our Prototype very soon."; 
  
  $message->send();
} catch (InvalidArgumentException $e) {
    $myresult = $e; 
}

/*
$headers = 'From: '.$email_from."\r\n".
 
'Reply-To: '.$email_from."\r\n" .
 
'X-Mailer: PHP/' . phpversion();
 
@mail($email_to, $email_subject, $email_message, $headers); 

 $myresult = "Thank you for applying !<br> We will send you an email for testing our Prototype very soon."; 
*/
}

?>
<!DOCTYPE html>
<!--[if lt IE 7 ]><html class="ie ie6" lang="en"> <![endif]-->
<!--[if IE 7 ]><html class="ie ie7" lang="en"> <![endif]-->
<!--[if IE 8 ]><html class="ie ie8" lang="en"> <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!-->
<html lang="en"> <!--<![endif]-->

	<head>
	
		<!-- Basic -->
		<meta charset="utf-8">
		<title>Snoozi | Thank God it's monday morning #TGIMM</title>
		<meta name="author" content="Snoozi">
		<meta name="keywords" content="wakeup, android, morning, monday, alarm, clock, video, snoozi">
		
		<meta name="description" content="Snoozi is a social alarm clock that connects People around the world. Users are able to send and receive Wake Up Videos from each other. Get Snoozi, and what awakes you in the morning will be a total surprise!"/>
		<meta name="image" content="http://snoozi.co/img/logo.png"/>
		
		<!-- for Facebook -->          
		<meta property="og:title" content="Snoozi - Thank God it's monday morning #TGIMM"/>
		<meta property="og:description" content="Snoozi is a social alarm clock that connects People around the world. Users are able to send and receive Wake Up Videos from each other. Get Snoozi, and what awakes you in the morning will be a total surprise!"/>
		<meta property="og:url" content="http://www.snoozi.co" />
		<meta property="og:image" content="http://snoozi.co/img/thumbnail.png"/>	
		
		<!-- for Twitter -->          
		<meta name="twitter:card" content="summary" />
		<meta name="twitter:title" content="Snoozi - Thank God it's monday morning #TGIMM" />
		<meta name="twitter:description" content="Snoozi is a social alarm clock that connects People around the world. Users are able to send and receive Wake Up Videos from each other. Get Snoozi, and what awakes you in the morning will be a total surprise!" />
		<meta name="twitter:image" content="http://snoozi.co/img/thumbnail.png" />


		<!-- Mobile Specific Metas -->
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">		
			
		<!-- Libs CSS -->
		<link href="css/bootstrap.css" rel="stylesheet"> 		
		<link href="css/font-awesome.min.css" rel="stylesheet">
		<link href="css/flexslider.css" rel="stylesheet">
		<link href="css/owl.carousel.css" rel="stylesheet">	
		
		<!-- On Scroll Animations -->
		<link href="css/animate.css" rel="stylesheet">
		
		<!-- Template CSS -->
		<link href="css/style.css" rel="stylesheet">  
		
		<!-- Responsive CSS -->
		<link href="css/responsive.css" rel="stylesheet"> 
								
		<!-- Favicons -->	
		<link rel="shortcut icon" href="img/icons/favicon.ico">
		<link rel="apple-touch-icon" sizes="114x114" href="img/icons/apple-touch-icon-114x114.png">
		<link rel="apple-touch-icon" sizes="72x72" href="img/icons/apple-touch-icon-72x72.png">
		<link rel="apple-touch-icon" href="img/icons/apple-touch-icon.png">
			
		<!-- Google Fonts -->	
		<link href='http://fonts.googleapis.com/css?family=Lato:400,900italic,900,700italic,400italic,300italic,300,100italic,100' rel='stylesheet' type='text/css'>
					
	</head>
		
	<body>
	
	
		<!-- CONTENT WRAPPER
		============================================= -->
		<div id="content-wrapper">
		
		
			<!-- INTRO
			============================================= -->
			<section id="intro" class="intro-parallax">
				<div class="container">				
				
					<!-- Header -->		
					<header id="header"> 
						<div class="row">	

							<!-- Logo Image -->
							<div id="logo_image" class="col-xs-6">
								<a href="#intro"><img src="img/logo.png" alt="logo" role="banner"></a>						
							</div>
						
							<!-- Header Social Icons -->
							<div id="social_icons" class="col-xs-6 text-right">						<!--											
								<ul class="social-icons clearfix">
									<li><a class="he_social ico-facebook" href="#"><i class="fa fa-facebook"></i></a></li>
									<li><a class="he_social ico-twitter" href="#"><i class="fa fa-twitter"></i></a></li>	
									<li><a class="he_social ico-google-plus" href="#"><i class="fa fa-google-plus"></i></a></li>							
												
									
									<li><a class="he_social ico-linkedin" href="#"><i class="fa fa-linkedin"></i></a></li>
									<li><a class="he_social ico-dribbble" href="#"><i class="fa fa-dribbble"></i></a></li>	
									<li><a class="he_social ico-instagram" href="#"><i class="fa fa-instagram"></i></a></li>
									<li><a class="he_social ico-pinterest" href="#"><i class="fa fa-pinterest"></i></a></li>	
									<li><a class="he_social ico-dropbox" href="#"><i class="fa fa-dropbox"></i></a></li>
									<li><a class="he_social ico-skype" href="#"><i class="fa fa-skype"></i></a></li>
									<li><a class="he_social ico-youtube" href="#"><i class="fa fa-youtube"></i></a></li>
									<li><a class="he_social ico-tumblr" href="#"><i class="fa fa-tumblr"></i></a></li>
									<li><a class="he_social ico-vimeo" href="#"><i class="fa fa-vimeo-square"></i></a></li>
									<li><a class="he_social ico-flickr" href="#"><i class="fa fa-flickr"></i></a></li>
									<li><a class="he_social ico-github" href="#"><i class="fa fa-github-alt"></i></a></li>
									<li><a class="he_social ico-renren" href="#"><i class="fa fa-renren"></i></a></li>
									<li><a class="he_social ico-vk" href="#"><i class="fa fa-vk"></i></a></li>
									<li><a class="he_social ico-xing" href="#"><i class="fa fa-xing"></i></a></li>
									<li><a class="he_social ico-weibo" href="#"><i class="fa fa-weibo"></i></a></li>
									<li><a class="he_social ico-rss" href="#"><i class="fa fa-rss"></i></a></li>										
								-->										
								</ul>
							</div>	 <!-- End Footer Social Icons -->	
							
						</div>
					</header>	<!-- End Header -->	
									
					<div class="row">
										
						<!-- Intro Section Description -->		
						<div id="intro_description" class="col-sm-7 col-md-7">
						
							<!-- Intro Section Title -->
							<h1><?php echo $myresult; ?></h1>
							
							<!-- Intro Section Button -->	
							<div class="intro_button text-center">
								<a class="btn btn-primary btn-lg" href="index.php">Back to home!</a>
							</div>	
						</div>	<!-- End Intro Section Description -->	
						
					
					</div>	<!-- End row -->	
					
				</div>	   <!-- End container -->		
			</section>  <!-- END INTRO -->
			
			
			
			
			
			<!-- FOOTER
			============================================= -->
			<footer id="footer">
				<div class="container">	
					<div class="row">
					
						<!-- Footer Navigation -->
						<div id="footer_nav" class="col-sm-6 col-md-4">
							<ul class="footer-nav clearfix">
								<li><a href="mailto:contact@snoozi.co">Contact</a></li>
								<li><a href="mailto:support@snoozi.co">Help</a></li>
								<!--<li><a href="#">Privacy</a></li>
								<li><a href="#">Terms</a></li> -->
							</ul>

							<div id="footer_copy">
								<p>&copy; Copyright 2014 Snoozi All Right Reserved</p>
							</div>							
						</div>	<!-- End Footer Navigation -->
						
						
						<!-- Footer Social Icons -->
						<div id="footer_icons" class="col-sm-6 col-md-4 text-center">																		
							<ul class="footer-socials clearfix"><!--
								<li><a class="foo_social ico-facebook" href="#"><i class="fa fa-facebook"></i></a></li>
								<li><a class="foo_social ico-twitter" href="#"><i class="fa fa-twitter"></i></a></li>	
								<li><a class="foo_social ico-google-plus" href="#"><i class="fa fa-google-plus"></i></a></li>
								<li><a class="foo_social ico-linkedin" href="#"><i class="fa fa-linkedin"></i></a></li>
								<li><a class="foo_social ico-dribbble" href="#"><i class="fa fa-dribbble"></i></a></li>
											
															
								<li><a class="foo_social ico-instagram" href="#"><i class="fa fa-instagram"></i></a></li>
								<li><a class="foo_social ico-pinterest" href="#"><i class="fa fa-pinterest"></i></a></li>	
								<li><a class="foo_social ico-dropbox" href="#"><i class="fa fa-dropbox"></i></a></li>
								<li><a class="foo_social ico-skype" href="#"><i class="fa fa-skype"></i></a></li>
								<li><a class="foo_social ico-youtube" href="#"><i class="fa fa-youtube"></i></a></li>
								<li><a class="foo_social ico-tumblr" href="#"><i class="fa fa-tumblr"></i></a></li>
								<li><a class="foo_social ico-vimeo" href="#"><i class="fa fa-vimeo-square"></i></a></li>
								<li><a class="foo_social ico-flickr" href="#"><i class="fa fa-flickr"></i></a></li>
								<li><a class="foo_social ico-github" href="#"><i class="fa fa-github-alt"></i></a></li>
								<li><a class="foo_social ico-renren" href="#"><i class="fa fa-renren"></i></a></li>
								<li><a class="foo_social ico-vk" href="#"><i class="fa fa-vk"></i></a></li>
								<li><a class="foo_social ico-xing" href="#"><i class="fa fa-xing"></i></a></li>
								<li><a class="foo_social ico-weibo" href="#"><i class="fa fa-weibo"></i></a></li>
								<li><a class="foo_social ico-rss" href="#"><i class="fa fa-rss"></i></a></li>										
							-->										
							</ul>
						</div>	 <!-- End Footer Social Icons -->	
						
						
					
					</div>	<!-- End row -->	
				</div>	  <!-- End container -->		
			</footer>	<!-- END FOOTER -->
			
		
		</div>	<!-- END CONTENT WRAPPER -->
	
	
		<!-- EXTERNAL SCRIPTS
		============================================= -->
		<script src="js/jquery-2.1.0.min.js" type="text/javascript"></script>
		<script src="js/bootstrap.min.js" type="text/javascript"></script>	
		<script src="js/retina.js" type="text/javascript"></script>	
		<script src="js/modernizr.custom.js" type="text/javascript"></script>	
		<script src="js/jquery.easing.js" type="text/javascript"></script>
		<script src="js/jquery.parallax-1.1.3.js" type="text/javascript"></script>
		<script src="js/jquery.validate.min.js" type="text/javascript"></script>
		<script defer src="js/jquery.flexslider.js" type="text/javascript"></script>
		<script src="js/jquery.accordion.source.js" type="text/javascript"></script>	
		<script src="js/owl.carousel.js" type="text/javascript"></script>
		<script src="js/waypoints.min.js" type="text/javascript"></script>	
		<script src="js/animations.js" type="text/javascript"></script>		
		<script src="js/custom.js" type="text/javascript"></script>
		

		<!--[if lt IE 9]>
		  <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
		  <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
		<![endif]-->
		
				

	
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-51005614-1', 'snoozi.co');
  ga('send', 'pageview');

</script>
	</body>

</html>

