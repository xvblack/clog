function secureSubmit(form){
  var inputUsername=form["username"];
  var inputPassword=form["password"];

  inputUsername.value;
  inputPassword.value=CryptoJS.SHA256(inputPassword.value);
}