import React from 'react';
import './web-page-form.component.css';


export class WebPageForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = { text: "" };
        this.input = React.createRef();
        this.handleClick = this.handleClick.bind(this);
        this.handleTextChange = this.handleTextChange.bind(this);
    }



    handleTextChange() {
        this.setState(state => ({
            text: this.input.current.value
        }));
    }

    handleClick(event) {
        if(typeof this.props.onSubmit === "function"){
            this.props.onSubmit(this.state.text);
        }
        event.preventDefault();
    }

    componentDidMount() {
    }

    componentWillUnmount() {
    }

    render() {
        const inputClassNames = "form__submit-button" + (this.props.isSubmitting === true) ? "": " form__submit-button--disabled";
        const inputLabel = this.props.isSubmitting === true ? "processing..." : "ok";

        return (
            <form className="form form--web-page" onSubmit={this.handleClick}>
                <input type="text" placeholder="Url here" className="form__input" value={this.state.text} onChange={this.handleTextChange} ref={this.input}/>
                <input className={inputClassNames} type="submit" value={inputLabel} />
            </form>
        );
    }
}