const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = {
    entry: './web/index.jsx',
    mode: 'production',
    plugins: [
        new MiniCssExtractPlugin({
            // Options similar to the same options in webpackOptions.output
            // all options are optional
            filename: 'stylesheets/[name].css',
            chunkFilename: '[id].css',
            ignoreOrder: false, // Enable to remove warnings about conflicting order
        })
    ],
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: ['babel-loader']
            },
            {
                test: /\.css$/,
                use: [
                    {
                        loader: MiniCssExtractPlugin.loader,
                        options: {
                            hmr: process.env.NODE_ENV === 'development',
                        },
                    },
                    'css-loader',
                ],
            }
        ]
    },
    resolve: {
        extensions: ['*', '.js', '.jsx'],
        alias: {
            '~': __dirname + '/web'
        }
    },
    output: {
        path: __dirname + '/public',
        publicPath: '/assets',
        filename: 'javascripts/main.js'
    },
    devServer: {
        contentBase: './dist'
    }
};