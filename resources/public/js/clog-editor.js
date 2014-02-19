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
  var post=document.getElementsByClassName("post-editor")[0];
  console.log(post);
  document.getElementById("post-post").innerText="saving";
  $.ajax({
    type:'POST',
    url: '/posts/'+post.dataset["id"],
    data:{
      title:post.getElementsByClassName("post-title-editor")[0].innerHTML,
      as:post.getElementsByClassName("post-as-editor")[0].innerHTML,
      content:cm.getValue()
    }
  }).done(function(data){
    console.log(data);
    document.getElementById("post-post").innerText="";
  });
}
