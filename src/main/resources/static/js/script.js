console.log("this is script file")

//SideBar
const toggleSidebar = () => {
	const $sidebar = $(".sidebar");
	const $content = $(".content");

	if ($sidebar.is(":visible")) {
		//true
		$sidebar.css("display", "none");
		$content.css("margin-left", "0%");
	} else {
		//false
		$sidebar.css("display", "block");
		$content.css("margin-left", "20%");
	}
};

//Search

const searchFun = () => {
	let filter = document.getElementById('myInput').value.toUpperCase();
	let myTable = document.getElementById('myTable');
	let tr = myTable.getElementsByTagName('tr');

	for (var i = 0; i < tr.length; i++) {
		let td = tr[i].getElementsByTagName('td')[0];

		if (td) {
			let textValue = td.textContent || td.innerHTML;

			if (textValue.toUpperCase().indexOf(filter) > -1) {
				tr[i].style.display = "";
			} else {
				tr[i].style.display = "none";
			}
		}
	}

	//searching with database

	let query = $("#myInput").val();

	if (query == '') {
		$(".search-result").hide();
	} else {

		let url = `http://localhost:8181/search/${query}`;

		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				//data
				//console.log(data);

				let text = `<div class='list-group'>`;

				data.forEach((contact) => {
					text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`
				});

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();

			});

	}

}

//Seaching with database

/*const search = () => {
	//console.log("searching...")

	let query = $("#myInput").val();

	if (query == '') {
		$(".search-result").hide();
	} else {

		let url = `http://localhost:8080/search/${query}`;

		fetch(url)
		.then((response) => {
			return response.json();
		})
		.then((data) => {
			//data
			console.log(data);
			
			let text = `<div class='list-group'>`;
			
			data.forEach((contact) =>{
				text += `<a href='#' class='list-group-item list-group-action'> ${contact.name} </a>`
			});
			
			text += `</div>`;
			
			$(".search-result").html(text);
			$(".search-result").show();
			
		});

	}
}*/