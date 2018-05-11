function onFileSelected(event) {
    var fd = new FormData();
    fd.append("image", event.target.files[0]);
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/register",
        data: fd,
        processData: false, //prevent jQuery from automatically transforming the data into a query string
        contentType: false,
        cache: false,
        success: function (data) {
            console.log("Success");
        },
        error: function (e) {
            console.log(e);
        }
    });
}