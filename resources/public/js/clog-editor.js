var cm;
$("document").ready(function(){
  var editor=document.getElementById("cm-editor");
  if (editor==undefined) return;
  cm=CodeMirror.fromTextArea(editor,{mode:"markdown",theme:"default",extraKeys:{Tab:function(cm) {
    var spaces = Array(cm.getOption("indentUnit") + 1).join(" ");
    cm.replaceSelection(spaces, "end", "+input");
  }}});
});

function savePost(post){
  console.log(post);
  var post=document.getElementById("post-editor");
  console.log(post);
  document.getElementById("post-post").innerText="saving";
  $.ajax({
    type:'POST',
    url: '/posts/'+document.getElementsByClassName("post-editor")[0].dataset["id"],
    data:{
      title:document.getElementsByClassName("post-title-editor")[0].innerHTML,
      content:cm.getValue()
    }
  }).done(function(data){
    console.log(data);
    document.getElementById("post-post").innerText="";
  });
}
