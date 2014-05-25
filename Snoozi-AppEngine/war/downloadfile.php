<?php

$file = 'gs://apkfile/Snoozi_v0.1.apk';
file_put_contents($file, 'hello world');

$apk_name = "Snoozi_v0.1.apk";
$web_inaccessible_dir = 'gs://apkfile';
echo filesize($web_inaccessible_dir.'/'.$apk_name);
header('Content-Disposition: attachment; filename='.$apk_name);
header('Content-Type: application/vnd.android.package-archive');
header('Content-Length: '.filesize($web_inaccessible_dir.'/'.$apk_name));
echo file_get_contents($web_inaccessible_dir.'/'.$apk_name);
	
	
?>