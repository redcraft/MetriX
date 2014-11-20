module.exports = function(grunt) {

	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		ngtemplates:  {
			metrix:        {
				src:      'src/main/webapp/components/*.html',
				dest:     'src/main/webapp/min/templates.js',
				options:  {
					url:    function(url) { return url.replace('src/main/webapp/', ''); }
				}
			}
		},
		uglify: {
			options: {
				banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n'
			},
			build: {
				src: ['src/main/webapp/libs/*.js', 'src/main/webapp/app.js', 'src/main/webapp/min/templates.js', 'src/main/webapp/components/*.js'],
				dest: 'src/main/webapp/min/app.min.js'
			}
		},
		less: {
			development: {
				options: {
					compress: true,
					yuicompress: true,
					optimization: 2
				},
				files: {
					"src/main/webapp/app.css": "src/main/webapp/app.less"
				}
			}
		}

	});

	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-angular-templates');
	grunt.loadNpmTasks('grunt-contrib-less');

	grunt.registerTask('default', ['ngtemplates', 'uglify', 'less']);
	grunt.registerTask('heroku', ['ngtemplates', 'uglify', 'less']);

};