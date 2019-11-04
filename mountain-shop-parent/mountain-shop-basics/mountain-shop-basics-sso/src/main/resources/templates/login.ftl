<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Insert title here</title>
	<link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css">
	<script src="https://cdn.bootcss.com/vue/2.5.17/vue.min.js"></script>
	<script src="https://unpkg.com/element-ui/lib/index.js"></script>
	<script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.js"></script>
</head>

<body>
<div class="login-box" id="app" >
   <el-form action="/auth/login" method="post" label-position="left" label-width="0px" class="demo-ruleForm login-container">
    <h2 class="title" >统一认证登录平台</h2>
    <el-form-item>
      <el-input type="text"  name="username" v-model="username" auto-complete="off" placeholder="账号"></el-input>
    </el-form-item>
    <el-form-item>
      <el-input type="password" name="password" v-model="password" auto-complete="off" placeholder="密码"></el-input>
    </el-form-item>
    <el-form-item style="width:100%; text-align:center;">
      <el-button type="primary" style="width:47%;" @click.native.prevent="reset">重 置</el-button>
      <el-button type="primary" style="width:47%;" native-type="submit" :loading="loading">登 录</el-button>
    </el-form-item>
  <el-form>
</div> 
</body>
 
<script type="text/javascript">
	new Vue({
		el : '#app',
		data : {
			loading: false,
	        username: 'admin',
	        password: '123'
		},
		methods : {
			reset: function() {
				this.username = 'admin'
				this.password = '123'
			}
		}
	})
	
</script>

<style lang="scss" scoped>
  .login-container {
    -webkit-border-radius: 5px;
    border-radius: 5px;
    -moz-border-radius: 5px;
    background-clip: padding-box;
    margin: 100px auto;
    width: 320px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
  }
  .title {
	  margin: 0px auto 20px auto;
	  text-align: center;
	  color: #505458;
	}
</style>

</html>
