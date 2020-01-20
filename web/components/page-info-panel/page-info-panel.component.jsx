import React from 'react';
import './page-info-panel.component.css';

export class PageInfoPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    componentDidMount() {
    }

    componentWillUnmount() {
    }

    renderField({fieldName, value}){
        if(value == null){
            return ""
        } else {
            return <div>
                <span> {fieldName} </span>
                <span> {value} </span>
            </div>
        }
    }

    renderTop(content){
        return <div className="panel panel--html-report">{content}</div>
    }

    render() {

        switch(this.props.info.status){

            case "none":
                return this.renderTop();
            case "submitting":
                return this.renderTop(<div>
                    <PageInfoField name="status" value="Processing"/>
                </div>);

            case "ok":
                return this.renderTop(<div>
                    <PageInfoField name="url" value={this.props.info.url}/>
                    <PageInfoField name="Document type" value={this.props.info.docType.fullName}/>
                    <PageInfoField name="Title" value={this.props.info.title}/>
                    <PageInfoField name="number of H1 headings" value={this.props.info.h1Headings}/>
                    <PageInfoField name="number of H2 headings" value={this.props.info.h2Headings}/>
                    <PageInfoField name="number of H3 headings" value={this.props.info.h3Headings}/>
                    <PageInfoField name="number of H4 headings" value={this.props.info.h4Headings}/>
                    <PageInfoField name="number of H5 headings" value={this.props.info.h5Headings}/>
                    <PageInfoField name="number of H6 headings" value={this.props.info.h6Headings}/>
                    <PageInfoField name="number of H6 headings" value={this.props.info.h6Headings}/>
                    <PageInfoField name="number of internal links" value={this.props.info.internalLinks}/>
                    <PageInfoField name="number of external links" value={this.props.info.externalLinks}/>
                    <PageInfoField name="number of inaccessible links" value={this.props.info.inaccessibleLinks}/>
                    <PageInfoField name="found Login Form " value={this.props.info.hasLoginForm ? "yes" : "no"}/>
                </div>);
            case "invalid":
                return this.renderTop(<div>
                    <PageInfoField name="url" value={this.props.info.url}/>
                    <PageInfoField name="reason" value={this.props.info.reason}/>
                </div>);
            case "error":
                return this.renderTop(<div>
                    <PageInfoField name="url" value={this.props.info.url}/>
                    <PageInfoField name="http status code" value={this.props.info.statusCode}/>
                </div>)

        }
    }
}

class PageInfoField extends React.Component {
    constructor(props) {
        super(props);
    }

    render(){
        return <div className="field field--html-report">
            <span className="field__label"> {this.props.name} </span>
            <span className="field__value"> {this.props.value} </span>
        </div>
    }
}

