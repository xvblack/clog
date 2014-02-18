/** @jsx React.DOM */
var ESCAPE_KEY = 27;
var ENTER_KEY = 13;
var TagsPicker=React.createClass({
  getInitialState:function(){
    return {tags:this.props.tags,
            editing:false,
            editText:"",
            postid:this.props.postid}
  },
  removeTag:function(i){
    console.log(i);
    var tags=this.state.tags.slice(0);
    tags.splice(i,1);
    this.setState({tags:tags});
  },
  handleChange:function(event){
    console.log(event.target.value);
    this.setState({editText:event.target.value});
  },
  handleKeyDown:function(event){
    console.log(event.keyCode);
    if (event.keyCode === ESCAPE_KEY){
      this.setState({editing:false,
                     editText:""});
    }
    if (event.keyCode === ENTER_KEY) {
      this.handleSubmit();
    }
  },
  handleEdit:function(event){
    this.setState({editing:true});
  },
  handleSubmit:function(event){
    var tag=this.state.editText;
    if (this.state.tags.indexOf(tag)!=-1){

    }else{
      var tags=this.state.tags.slice(0);
      tags.push(tag);
      console.log(tags);
      this.setState({
        tags:tags,
        editing:false,
        editText:""
      });
    }
  },
  componentWillUpdate:function(nextProps, nextState){
    if(nextState.tags!=this.state.tags){
      this.updateTags(nextState);
    }
  },
  updateTags:function(state){
    console.log("save to"+'/posts/'+this.props.postid);
    $.ajax({
      url:'/posts/'+this.props.postid,
      type:'POST',
      data:{tags:state.tags},
      success:function(){
        console.log("tag saved");
      }
    })
  },
  render:function(){
    var self=this;
    var i=0;
    return(
      <div className="tagPicker">
        <ul>
        {this.state.tags.map(function(tag){
          return(<li>
                   <span>{tag}</span>
                   <span onClick={self.removeTag.bind(self,i++)}>ⅹ</span>
                 </li>);
        })}
                 <li style={{visibility:(!this.state.editing?"":"hidden")}}>
                 <span onClick={this.handleEdit}>+</span>
                 </li>
                          <li style={{visibility:(this.state.editing?"":"hidden")}}>
                          <input contentEditable={true} onChange={this.handleChange} onKeyDown={this.handleKeyDown} value={this.state.editText}></input>
        </li>
        </ul>
      </div>
    );
  }
})
var tags=["aa","bb"];
var datas=document.getElementsByClassName("post-editor")[0].dataset;
var comp=React.renderComponent(
  <TagsPicker tags={JSON.parse(datas["tags"])} postid={datas["id"]}/>,
  document.getElementById('picker')
);
