<!DOCTYPE html>
<html>
	<script src="http://code.jquery.com/jquery.min.js"></script>
	<?php
		if (isset($_POST['upVolume'])) {
	?>
			<body onload="changeVolume(1)">
			<?php
		} else if (isset($_POST['downVolume'])) {
			?>
			<body onload="changeVolume(-1)">
	<?php
		}
	?>
	</body>
	<script>
		function changeVolume(plusminus) {
			var baseURL = "http://" + "192.168.1.14" + ":8090";
			var getURL = baseURL + "/volume";
			console.log(getURL);
			$.get(getURL, {}).done(function(xml) {
				var actualVolume = parseInt($(xml).find("actualvolume").first().text());
				var newVolume = actualVolume+plusminus;
				var postURL = "http://" + "192.168.1.14" + ":8090";
				var data = "<volume>" + newVolume.toString() + "</volume>";
				console.log(actualVolume);
				console.log(newVolume);
				console.log(data);
				$.ajax({
					url: postURL + "/volume",
					type: 'POST',
					crossDomain: true,
					data: data,
					dataType: 'text',
					success: function(result) {
						console.log("Yay");
					},
					error: function(jqXHR, transStatus, errorThrown) {
						alert('Status: ' + jqXHR.status + ' ' + jqXHR.statusText + '.' +
						'Response: ' + jqXHR.responseText);
					}
				});

			});
		}
	</script>
</html>