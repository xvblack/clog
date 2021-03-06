var cm;
$("document").ready(function(){
  var editor=document.getElementsByClassName("cm-editor")[0];
  if (editor==undefined) return;
  cm=CodeMirror.fromTextArea(editor,
                             {mode:"markdown",
                              theme:"default",
                              extraKeys:{Tab:function(cm) {
                                var spaces = Array(cm.getOption("indentUnit") + 1).join(" ");
                                cm.replaceSelection(spaces, "end", "+input");
                              }},
                              lineWrapping: true});
  inlineOptions={
    uploadUrl: "/upload",
    allowedTypes: [
      'image/jpeg',
      'image/png',
      'image/jpg',
      'image/gif'
    ],
    progressText: '![Uploading file...]()',
    urlText: "![file]({filename})",
    errorText: "Error uploading file"
  };
  inlineAttach.attachToCodeMirror(cm,inlineOptions);
});

function savePost(post){
  var post=document.getElementsByClassName("post-editor")[0];
  console.log(post);
  document.getElementsByClassName("post-post")[0].innerText="saving";
  $.ajax({
    type:'POST',
    url: '/posts/'+post.dataset["id"],
    data:{
      title:post.getElementsByClassName("post-title-editor")[0].innerHTML,
      as:post.getElementsByClassName("post-as-editor")[0].innerHTML,
      publish:post.getElementsByClassName("publish-check")[0].checked,
      content:cm.getValue()
    }
  }).done(function(data){
    console.log(data);
    document.getElementsByClassName("post-post")[0].innerText="";
  });
}
